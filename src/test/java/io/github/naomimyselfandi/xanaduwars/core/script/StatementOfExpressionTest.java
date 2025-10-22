package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementOfExpressionTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private EvaluationContext context;

    @BeforeEach
    void setup() {
        when(context.getRootObject()).thenReturn(new TypedValue(new Object()));
    }

    @Test
    void execute(SeededRng random) {
        var name = "_" + random.nextString();
        var value = random.nextString();
        when(context.lookupVariable(name)).thenReturn(value);
        assertThat(new StatementOfExpression("#" + name).execute(runtime, context)).isEqualTo(value);
    }

    @Test
    void execute_WhenEvaluatingTheExpressionFails_ThenThrows(SeededRng random) {
        var invalidExpression = "_%s._%s".formatted(random.nextInt(), random.nextInt());
        assertThatThrownBy(() -> new StatementOfExpression(invalidExpression).execute(runtime, context))
                .isInstanceOf(EvaluationException.class)
                .hasMessage("Failed evaluating %s.".formatted(invalidExpression))
                .cause()
                .isInstanceOf(EvaluationException.class);
    }

}
