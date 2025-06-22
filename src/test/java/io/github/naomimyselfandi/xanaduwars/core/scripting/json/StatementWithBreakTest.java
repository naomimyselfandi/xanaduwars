package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class StatementWithBreakTest {

    private interface Helper {}

    @Mock
    private EvaluationContext context;

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    void execute(int depth) {
        assertThat(new StatementWithBreak(depth).execute(context, Helper.class)).isEqualTo(new Statement.Break(depth));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5})
    void testToString(int depth) {
        assertThat(new StatementWithBreak(depth)).hasToString("break %d", depth);
    }

}
