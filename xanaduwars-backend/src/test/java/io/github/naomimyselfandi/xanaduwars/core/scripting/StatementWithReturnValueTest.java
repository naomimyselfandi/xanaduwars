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
class StatementWithReturnValueTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression expression;

    @InjectMocks
    private StatementWithReturnValue fixture;

    @Test
    void execute() {
        var value = new Object();
        when(expression.getValue(context)).thenReturn(value);
        assertThatThrownBy(() -> fixture.execute(context)).isEqualTo(new Statement.Return(value));
    }

    @Test
    void testToString(SeededRng random) {
        var expressionString = random.nextString();
        when(expression.getExpressionString()).thenReturn(expressionString);
        assertThat(fixture).hasToString("return %s", expressionString);
    }

}
