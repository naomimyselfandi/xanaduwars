package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackDamageQueryTest {

    @Mock
    private UnitType unitType, anotherUnitType;

    @Mock
    private Unit subject, targetUnit;

    @Mock
    private Structure targetStructure;

    @Mock
    private Weapon weapon;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultValue_Unit(boolean counter, SeededRng random) {
        var value = random.nextInt();
        when(targetUnit.getType()).thenReturn(unitType);
        when(weapon.getDamage()).thenReturn(Map.of(unitType, value, anotherUnitType, random.nextInt()));
        assertThat(new AttackDamageQuery(subject, weapon, targetUnit, counter).defaultValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultValue_Structure(boolean counter, SeededRng random) {
        var value = random.nextInt(50, 100);
        var tag0 = random.<StructureTag>get();
        var tag1 = random.not(tag0);
        var tag2 = random.not(tag0, tag1);
        var tag3 = random.not(tag0, tag1, tag2);
        when(targetStructure.getTags()).thenReturn(Set.of(tag0, tag1));
        when(weapon.getDamage()).thenReturn(Map.of(
                tag0, value,
                tag1, random.nextInt(0, value),
                tag2, random.nextInt(),
                tag3, random.nextInt(),
                unitType, random.nextInt()
        ));
        assertThat(new AttackDamageQuery(subject, weapon, targetStructure, counter).defaultValue()).isEqualTo(value);
    }

}
