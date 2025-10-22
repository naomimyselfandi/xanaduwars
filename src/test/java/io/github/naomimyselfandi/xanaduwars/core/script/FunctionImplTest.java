package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class FunctionImplTest {

    @Captor
    private ArgumentCaptor<EvaluationContext> captor;

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private EvaluationContext closure;

    private String name, foo, bar;

    @Mock
    private Statement instruction;

    private FunctionImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        name = random.nextString();
        foo = random.not(name);
        bar = random.not(name, foo);
        fixture = new FunctionImpl(runtime, closure, name, List.of(foo, bar), instruction);
    }

    @Test
    void call() {
        var value = new Object();
        var fooValue = new Object();
        var barValue = new Object();
        when(instruction.execute(eq(runtime), captor.capture())).thenReturn(value);
        assertThat(fixture.call(fooValue, barValue)).isEqualTo(value);
        assertThat(captor.getValue()).isInstanceOfSatisfying(ScriptEvaluationContext.class, it -> {
            assertThat(it.parent).isEqualTo(closure);
            assertThat(it.variables).isEqualTo(Map.of(foo, fooValue, bar, barValue));
        });
    }

    @Test
    void call_WhenTheCallFails_ThenThrows() {
        var exception = new RuntimeException();
        var fooValue = new Object();
        var barValue = new Object();
        when(instruction.execute(eq(runtime), any())).thenThrow(exception);
        assertThatThrownBy(() -> fixture.call(fooValue, barValue))
                .hasMessage("Calling %s with [%s, %s] failed.", fixture, fooValue, barValue)
                .hasCause(exception);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3})
    void call_WhenTheArgumentCountIsWrong_ThenThrows(int arity) {
        var arguments = IntStream.range(0, arity).mapToObj(_ -> new Object()).toArray();
        assertThatThrownBy(() -> fixture.call(arguments))
                .hasMessage("Calling %s with %s failed.", fixture, Arrays.toString(arguments))
                .cause()
                .isInstanceOf(EvaluationException.class)
                .hasMessage("Wrong number of arguments.");
        verifyNoInteractions(instruction);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("%s(%s, %s)", name, foo, bar);
        fixture = new FunctionImpl(runtime, closure, name, List.of(foo), instruction);
        assertThat(fixture).hasToString("%s(%s)", name, foo);
        fixture = new FunctionImpl(runtime, closure, name, List.of(), instruction);
        assertThat(fixture).hasToString("%s()", name);
    }

}
