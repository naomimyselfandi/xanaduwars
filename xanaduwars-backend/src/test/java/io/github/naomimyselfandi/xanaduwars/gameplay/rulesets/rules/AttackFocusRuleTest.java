package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.AttackStage;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackFocusRuleTest {

    @Mock
    private Unit attacker, defender;

    @Mock
    private Player attackerOwner, defenderOwner;

    @Mock
    private UnitType defenderType;

    @Mock
    private Filter<Unit> subjectFilter;

    @Mock
    private BiFilter<Unit, Node> targetFilter;

    private AttackFocusRule fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new AttackFocusRule(
                subjectFilter,
                targetFilter,
                random.nextPercent(),
                random.nextPercent(),
                random.pick(Resource.SUPPLIES, Resource.AETHER)
        );
    }

    @RepeatedTest(10)
    void handle(SeededRng random) {
        var attackerHasOwner = random.nextBoolean();
        var attackerFocus = random.nextInt(1000);
        if (attackerHasOwner) {
            when(attacker.owner()).thenReturn(Optional.of(attackerOwner));
            when(attackerOwner.resources()).thenReturn(Map.of(Resource.FOCUS, attackerFocus));
        }
        var defenderHasOwner = random.nextBoolean();
        var defenderFocus = random.nextInt(1000);
        if (defenderHasOwner) {
            when(defender.owner()).thenReturn(Optional.of(defenderOwner));
            when(defenderOwner.resources()).thenReturn(Map.of(Resource.FOCUS, defenderFocus));
        }
        var cost = random.nextInt(1000);
        when(defender.type()).thenReturn(defenderType);
        when(defenderType.costs()).thenReturn(Map.of(fixture.resource(), cost));
        var stage = random.pick(AttackStage.values());
        var event = new AttackEvent(attacker, defender, stage, random.nextScalar(), random.nextPercent());
        assertThat(fixture.handle(event, None.NONE)).isEqualTo(None.NONE);
        if (attackerHasOwner) {
            var multiplier = event.actualDamage().doubleValue() * fixture.attackerMultiplier().doubleValue();
            var total = attackerFocus + (cost * multiplier);
            verify(attackerOwner).resources(); // For verifyNoMoreInteractions();
            verify(attackerOwner).resource(Resource.FOCUS, (int) Math.round(total));
        }
        if (defenderHasOwner) {
            var multiplier = event.actualDamage().doubleValue() * fixture.defenderMultiplier().doubleValue();
            var total = defenderFocus + (cost * multiplier);
            verify(defenderOwner).resources(); // For verifyNoMoreInteractions();
            verify(defenderOwner).resource(Resource.FOCUS, (int) Math.round(total));
        }
        verifyNoMoreInteractions(attackerOwner, defenderOwner);
    }

}
