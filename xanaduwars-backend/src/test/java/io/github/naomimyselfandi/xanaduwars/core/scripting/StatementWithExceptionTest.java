package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithExceptionTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression expression;

    @InjectMocks
    private StatementWithException fixture;

    @Test
    void execute(SeededRng random) {
        var message = random.nextString();
        when(expression.getValue(context)).thenReturn(message);
        assertThatThrownBy(() -> fixture.execute(context))
                .isInstanceOf(ScriptingException.class)
                .hasMessage(message);
    }

    @Test
    void execute_ToleratesNull() {
        assertThatThrownBy(() -> fixture.execute(context))
                .isInstanceOf(ScriptingException.class)
                .hasMessage("null");
    }

    @Test
    void testToString(SeededRng random) {
        var message = random.nextString();
        when(expression.getExpressionString()).thenReturn(message);
        assertThat(fixture).hasToString("throw %s", message);
    }

}
