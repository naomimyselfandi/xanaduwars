package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithContinueTest {

    @Mock
    private EvaluationContext context;

    private StatementWithContinue fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new StatementWithContinue(random.nextInt(1, 10));
    }

    @Test
    void execute() {
        assertThatThrownBy(() -> fixture.execute(context)).isEqualTo(new Statement.Continue(fixture.depth()));
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("continue %d", fixture.depth());
    }

}
