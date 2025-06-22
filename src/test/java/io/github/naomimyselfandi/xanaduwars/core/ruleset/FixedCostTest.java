package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class FixedCostTest {

    @Mock
    private EvaluationContext context;

    private FixedCost fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = random.get();
    }

    @Test
    void run() {
        assertThat(fixture.run(context, Integer.class)).isEqualTo(fixture.ordinal());
        verifyNoInteractions(context);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString(String.valueOf(fixture.ordinal()));
    }

    @Test
    void json() {
        TestUtils.assertJson(fixture, String.valueOf(fixture.ordinal()));
    }

}
