package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithBreakTest {

    @Mock
    private EvaluationContext context;

    private StatementWithBreak fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new StatementWithBreak(random.nextInt(1, 10));
    }

    @Test
    void execute() {
        assertThatThrownBy(() -> fixture.execute(context)).isEqualTo(new Statement.Break(fixture.depth()));
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("break %d", fixture.depth());
    }

}
