package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackCalculationTest {

    @Mock
    private UnitType defenderType;

    @Mock
    private Unit attacker, defender;

    private AttackCalculation fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(defender.type()).thenReturn(defenderType);
        fixture = new AttackCalculation(attacker, defender, random.pick(AttackStage.values()));
    }

    @RepeatedTest(4)
    void defaultValue(SeededRng random) {
        var expected = random.nextPercent();
        when(attacker.damageTable()).thenReturn(Map.of(defenderType, expected));
        assertThat(fixture.defaultValue()).isEqualTo(expected);
    }

    @Test
    void defaultValue_WhenTheTypeIsNotInTheTable_ThenFalse() {
        when(attacker.damageTable()).thenReturn(Map.of());
        assertThat(fixture.defaultValue()).isEqualTo(Percent.ZERO);
    }

}
