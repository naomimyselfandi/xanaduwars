package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetValidatorForWeaponTest {

    @Mock
    private UnitType unitType0, unitType1;

    @Mock
    private Structure structure;

    @Mock
    private Unit attacker, unit;

    @Mock
    private Weapon weapon;

    @InjectMocks
    private TargetValidatorForWeapon fixture;

    @Test
    void fail() {
        assertThat(fixture.fail()).isEqualTo(Result.fail("Cannot attack that with this weapon."));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_StructureTarget(boolean valid, SeededRng random) {
        var tag0 = random.<StructureTag>get();
        var tag1 = random.not(tag0);
        var tag2 = random.not(tag0, tag1);
        var tag3 = valid ? tag0 : random.not(tag0, tag1, tag2);
        when(structure.getTags()).thenReturn(Set.of(tag0, tag1));
        when(weapon.getDamage()).thenReturn(Map.of(tag2, random.get(), tag3, random.get()));
        assertThat(fixture.test(attacker, weapon, structure, random.get())).isEqualTo(valid);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_UnitTarget(boolean valid, SeededRng random) {
        when(unit.getType()).thenReturn(unitType0);
        var unitType2 = valid ? unitType0 : unitType1;
        when(weapon.getDamage()).thenReturn(Map.of(unitType2, random.get()));
        assertThat(fixture.test(attacker, weapon, unit, random.get())).isEqualTo(valid);
    }

}
