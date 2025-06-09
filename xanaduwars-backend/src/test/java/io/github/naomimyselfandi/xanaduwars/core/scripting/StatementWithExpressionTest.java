package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.SneakyThrows;
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
class StatementWithExpressionTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression expression;

    @InjectMocks
    private StatementWithExpression fixture;

    @Test
    @SneakyThrows
    void execute() {
        fixture.execute(context);
        verify(expression).getValue(context);
    }

    @Test
    void testToString(SeededRng random) {
        var expressionString = random.nextString();
        when(expression.getExpressionString()).thenReturn(expressionString);
        assertThat(fixture).hasToString(expressionString);
    }

}
