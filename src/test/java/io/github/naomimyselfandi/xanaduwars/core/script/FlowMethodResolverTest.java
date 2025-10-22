package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class FlowMethodResolverTest {

    @Mock
    private EvaluationContext context;

    private final FlowMethodResolver fixture = new FlowMethodResolver();

    @ParameterizedTest
    @ValueSource(classes = {int.class, Integer.class})
    void resolve_Goto(Class<?> type, SeededRng random) throws AccessException {
        var value = random.nextInt();
        var executor = fixture.resolve(context, new Object(), "goto", List.of(TypeDescriptor.valueOf(type)));
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, new Object(), value)).isEqualTo(TypedValue.NULL);
        verify(context).setVariable(StatementOfBlock.TARGET_VARIABLE, value);
        verifyNoMoreInteractions(context);
    }

    @Test
    void resolve_Return() throws AccessException {
        var executor = fixture.resolve(context, new Object(), "return", List.of());
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, new Object())).isEqualTo(TypedValue.NULL);
        verify(context).setVariable(StatementOfBlock.TARGET_VARIABLE, Integer.MAX_VALUE);
        verifyNoMoreInteractions(context);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(classes = {Integer.class, Boolean.class, Double.class, String.class, Object.class})
    void resolve_Return(@Nullable Class<?> type, SeededRng random) throws AccessException {
        var value = type == null ? null : random.get(type);
        var types = new ArrayList<TypeDescriptor>(1);
        types.add(TypeDescriptor.forObject(value));
        var executor = fixture.resolve(context, new Object(), "return", types);
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, new Object(), value)).isEqualTo(TypedValue.NULL);
        verify(context).setVariable(StatementOfBlock.TARGET_VARIABLE, Integer.MAX_VALUE);
        verify(context).setVariable(StatementOfBlock.RESULT_VARIABLE, value);
        verifyNoMoreInteractions(context);
    }

    @MethodSource
    @ParameterizedTest
    void resolve_Unknown(Object targetObject, String name, List<Class<?>> types) {
        var argumentTypes = types.stream().map(TypeDescriptor::valueOf).toList();
        var executor = fixture.resolve(context, targetObject, name, argumentTypes);
        assertThat(executor).isNull();
    }

    private static Stream<Arguments> resolve_Unknown() {
        return Stream.of(
                arguments(Undefined.UNDEFINED, "goto", List.of()),
                arguments(Undefined.UNDEFINED, "goto", List.of(Object.class)),
                arguments(Undefined.UNDEFINED, "goto", List.of(int.class, int.class)),
                arguments(Undefined.UNDEFINED, "return", List.of(Object.class, Object.class))
        );
    }

}
