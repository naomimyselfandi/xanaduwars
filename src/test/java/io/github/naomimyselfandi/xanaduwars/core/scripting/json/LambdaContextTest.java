package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SeededRandomExtension.class, MockitoExtension.class})
class LambdaContextTest {

    private @Mock EvaluationContext parent;

    private String p0, p1, p2;
    private List<String> parameters;

    private Object a0, a1, a2;
    private List<Object> arguments;

    private LambdaContext fixture;

    @BeforeEach
    void setup(SeededRng random) {
        p0 = random.nextString();
        p1 = random.not(p0);
        p2 = random.not(p0, p1);
        parameters = List.of(p0, p1, p2);
        a0 = random.<UUID>get();
        a1 = random.not(a0);
        a2 = random.not(a0, a1);
        arguments = List.of(a0, a1, a2);
        this.fixture = new LambdaContext(parent, parameters, arguments);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void lookupVariable_WhenTheVariableIsAmongTheParameterNames_ThenReturnsTheCorrespondingArgument(int i) {
        assertThat(fixture.lookupVariable(parameters.get(i))).isEqualTo(arguments.get(i));
    }

    @Test
    void lookupVariable_WhenTheVariableIsNotAmongTheParameterNames_ThenChecksTheParent(SeededRng random) {
        var name = random.not(parameters.toArray(String[]::new));
        var value = new Object();
        when(parent.lookupVariable(name)).thenReturn(value);
        assertThat(fixture.lookupVariable(name)).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void setVariable_WhenTheVariableIsAmongTheParameterNames_ThenThrows(int i) {
        assertThatThrownBy(() -> fixture.setVariable(parameters.get(i), new Object()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void setVariable_WhenTheVariableIsNotAmongTheParameterNames_ThenChecksTheParent(SeededRng random) {
        var name = random.not(parameters.toArray(String[]::new));
        var value = new Object();
        assertThatCode(() -> fixture.setVariable(name, value)).doesNotThrowAnyException();
        verify(parent).setVariable(name, value);
    }

    @Test
    void testToString() {
        var template = "{%s=%s, %s=%s, %s=%s, ...%s}";
        assertThat(fixture).hasToString(template, p0, a0, p1, a1, p2, a2, parent);
    }

}
