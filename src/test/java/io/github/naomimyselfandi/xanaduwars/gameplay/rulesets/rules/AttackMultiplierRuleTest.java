package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Scalar;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackMultiplierRuleTest {

    @Mock
    private Unit attacker, defender;

    private double multiplier;

    private AttackMultiplierRule fixture;

    @BeforeEach
    void setup(SeededRng random) {
        multiplier = random.nextDouble();
        fixture = new AttackMultiplierRule(mock(), mock(), Scalar.withDoubleValue(multiplier));
    }

    @RepeatedTest(3)
    void handle(SeededRng random) {
        var base = random.nextDouble();
        var expected = Scalar.withDoubleValue(base * multiplier);
        var query = new AttackCalculation(attacker, defender, random.pick(AttackStage.values()));
        assertThat(fixture.handle(query, Scalar.withDoubleValue(base))).isEqualTo(expected);
    }

}
