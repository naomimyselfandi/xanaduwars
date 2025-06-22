package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptingException;
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

    private interface Helper {}

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
        assertThatThrownBy(() -> fixture.execute(context, Helper.class))
                .isInstanceOf(ScriptingException.class)
                .hasMessage(message);
    }

    @Test
    void execute_ToleratesNull() {
        assertThatThrownBy(() -> fixture.execute(context, Helper.class))
                .isInstanceOf(ScriptingException.class)
                .hasMessage("null");
    }

    @Test
    void testToString(SeededRng random) {
        when(expression.getExpressionString()).thenReturn(random.nextString());
        assertThat(fixture).hasToString("throw %s", expression.getExpressionString());
    }

}
