package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

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

    private interface Helper {}

    @Mock
    private Helper helper;

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression expression;

    @InjectMocks
    private StatementWithReturnValue fixture;

    @Test
    void execute() {
        when(expression.getValue(context, Helper.class)).thenReturn(helper);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(helper);
    }

    @Test
    void testToString(SeededRng random) {
        when(expression.getExpressionString()).thenReturn(random.nextString());
        assertThat(fixture).hasToString("return %s", expression.getExpressionString());
    }

}
