package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VariableAccessorTest {

    @Mock
    private EvaluationContext context;

    private VariableAccessor fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
        fixture = new VariableAccessor();
    }

    @ParameterizedTest
    @MethodSource("accessCheck")
    void canRead(@Nullable Object target, boolean expected) {
        assertThat(fixture.canRead(context, target, random.nextString())).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void read(boolean nullValue) throws AccessException {
        var name = random.nextString();
        var value = nullValue ? null : new Object();
        when(context.lookupVariable(name)).thenReturn(value);
        assertThat(fixture.read(context, Undefined.UNDEFINED, name)).isEqualTo(new TypedValue(value));
    }

    @Test
    void read_WhenTheVariableIsUndefined_ThenThrows(SeededRng random) {
        var name = random.nextString();
        when(context.lookupVariable(name)).thenReturn(Undefined.UNDEFINED);
        assertThatThrownBy(() -> fixture.read(context, Undefined.UNDEFINED, name))
                .isInstanceOf(AccessException.class)
                .hasMessage("Unknown variable '%s'.".formatted(name));
    }

    @ParameterizedTest
    @MethodSource("accessCheck")
    void canWrite(@Nullable Object target, boolean expected) {
        assertThat(fixture.canWrite(context, target, random.nextString())).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void write(boolean nullValue) throws AccessException {
        var name = random.nextString();
        var value = random.nextString();
        when(context.lookupVariable(name)).thenReturn(nullValue ? null : random.nextString());
        fixture.write(context, Undefined.UNDEFINED, name, value);
        verify(context).setVariable(name, value);
    }

    @Test
    void write_WhenTheVariableIsUndefined_ThenThrows() {
        var name = random.nextString();
        var value = random.nextString();
        when(context.lookupVariable(name)).thenReturn(Undefined.UNDEFINED);
        assertThatThrownBy(() -> fixture.write(context, Undefined.UNDEFINED, name, value))
                .isInstanceOf(AccessException.class)
                .hasMessage("Unknown variable '%s'.".formatted(name));
        verify(context, never()).setVariable(name, value);
    }

    @Test
    void getSpecificTargetClasses() {
        assertThat(fixture.getSpecificTargetClasses()).containsExactly(Undefined.class);
    }

    private static Stream<Arguments> accessCheck() {
        return Stream.of(
                arguments(Undefined.UNDEFINED, true),
                arguments(new Object(), false),
                arguments(null, false)
        );
    }

}
