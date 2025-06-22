package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StatementWithContinueTest {

    private interface Helper {}

    @Mock
    private EvaluationContext context;

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    void execute(int depth) {
        Statement statement = new StatementWithContinue(depth);
        assertThat(statement.execute(context, Helper.class)).isEqualTo(new Statement.Continue(depth));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5})
    void testToString(int depth) {
        assertThat(new StatementWithContinue(depth)).hasToString("continue %d", depth);
    }

}
