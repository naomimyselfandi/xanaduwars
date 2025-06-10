package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.*;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.*;

class RulesetServiceImplTest {

    @Test
    void load() {
        var service = new RulesetServiceImpl("testRuleset", new ObjectMapper());
        var ruleset = service.load(new Version("1.2.2"));
        assertThat(ruleset).isNotNull();
        var commanders = ruleset.getCommanders().toArray(Commander[]::new);
        assertThat(commanders).hasSize(2);
        validateIds(CommanderId::new, Commander::getId, commanders);
        var spells = ruleset.getSpells().toArray(Spell[]::new);
        assertThat(spells).hasSize(8);
        validateIds(SpellId::new, Spell::getId, spells);
        var structureTypes = ruleset.getStructureTypes().toArray(StructureType[]::new);
        assertThat(structureTypes).hasSize(6);
        validateIds(StructureTypeId::new, StructureType::getId, structureTypes);
        var tileTypes = ruleset.getTileTypes().toArray(TileType[]::new);
        assertThat(tileTypes).hasSize(17);
        validateIds(TileTypeId::new, TileType::getId, tileTypes);
        var unitTypes = ruleset.getUnitTypes().toArray(UnitType[]::new);
        validateIds(UnitTypeId::new, UnitType::getId, unitTypes);
        assertThat(unitTypes).hasSize(19);
        assertThat(commanders[0].getName()).isEqualTo(new Name("Alice"));
        assertThat(commanders[0].getSignatureSpells()).containsExactly(spells[0]);
        assertThat(commanders[0].getAffinities()).hasSize(2)
                .containsEntry(new SpellTag("Offensive"), Affinity.POSITIVE)
                .containsEntry(new SpellTag("Defensive"), Affinity.NEGATIVE);
        assertThat(commanders[1].getName()).isEqualTo(new Name("Bob"));
        assertThat(commanders[1].getSignatureSpells()).containsExactly(spells[1]);
        assertThat(commanders[1].getAffinities()).hasSize(2)
                .containsEntry(new SpellTag("Defensive"), Affinity.POSITIVE)
                .containsEntry(new SpellTag("Offensive"), Affinity.NEGATIVE);
        assertThat(spells[0].getName()).isEqualTo(new Name("MagicMissile"));
        assertThat(spells[0].getTargets()).containsExactly(new TargetSpec.UnitTargetSpec(IFF.FOE, 0, 0));
        assertThat(spells[0].getFocusCost()).isEqualTo(4000);
        assertThat(spells[0].getTags()).contains(new SpellTag("Offensive"));
        assertThat(spells[1].getName()).isEqualTo(new Name("QuickFix"));
        assertThat(spells[1].getTargets()).containsExactly(new TargetSpec.TileTargetSpec(true, 0, 0));
        assertThat(spells[1].getFocusCost()).isEqualTo(2400);
        assertThat(spells[1].getTags()).contains(new SpellTag("Defensive"));
        assertThat(spells[2].getName()).isEqualTo(new Name("HammerForce"));
        assertThat(spells[2].getTargets()).isEmpty();
        assertThat(spells[2].getFocusCost()).isEqualTo(1600);
        assertThat(spells[2].getTags()).contains(new SpellTag("Offensive"));
        assertThat(structureTypes[0].getName()).isEqualTo(new Name("Headquarters"));
        assertThat(structureTypes[0].getTags()).contains(new StructureTag("Structure"));
        assertThat(structureTypes[0].getFoundation()).isEqualTo(tileTypes[0]);
        assertThat(structureTypes[0].getSupplyIncome()).isEqualTo(100);
        assertThat(structureTypes[0].getSupplyCost()).isEqualTo(1000);
        assertThat(structureTypes[0].getBuildTime()).isEqualTo(10000);
        assertThat(structureTypes[0].getVision()).isEqualTo(2);
        assertThat(structureTypes[0].getMovementTable()).isEqualTo(new MovementTable(Map.of(
                new UnitTag("Land"), 1.0,
                new UnitTag("Air"), 1.0
        )));
        assertThat(structureTypes[0].getCover()).isEqualTo(0.4);
        assertThat(structureTypes[3].getName()).isEqualTo(new Name("Fort"));
        assertThat(structureTypes[3].getTags()).contains(new StructureTag("Structure"));
        assertThat(structureTypes[3].getFoundation()).isEqualTo(tileTypes[13]);
        assertThat(structureTypes[3].getSupplyIncome()).isZero();
        assertThat(structureTypes[3].getSupplyCost()).isEqualTo(300);
        assertThat(structureTypes[3].getBuildTime()).isEqualTo(200);
        assertThat(structureTypes[3].getVision()).isZero();
        assertThat(structureTypes[3].getMovementTable()).isEqualTo(new MovementTable(Map.of(
                new UnitTag("Land"), 1.0,
                new UnitTag("Air"), 1.0
        )));
        assertThat(structureTypes[3].getDeploymentRoster()).containsExactly(
                unitTypes[0],
                unitTypes[1],
                unitTypes[2],
                unitTypes[3],
                unitTypes[4],
                unitTypes[5],
                unitTypes[6],
                unitTypes[7],
                unitTypes[8],
                unitTypes[9],
                unitTypes[10]
        );
        assertThat(structureTypes[3].getCover()).isEqualTo(0.3);
        assertThat(tileTypes[0].getName()).isEqualTo(new Name("Plains"));
        assertThat(tileTypes[0].getTags()).isEmpty();
        assertThat(tileTypes[0].getMovementTable()).isEqualTo(new MovementTable(Map.of(
                new UnitTag("Treaded"), 1.0,
                new UnitTag("Wheeled"), 1.5,
                new UnitTag("Soldier"), 1.0,
                new UnitTag("Air"), 1.0
        )));
        assertThat(tileTypes[0].getCover()).isEqualTo(0.1);
        assertThat(tileTypes[1].getName()).isEqualTo(new Name("Road"));
        assertThat(tileTypes[1].getTags()).isEmpty();
        assertThat(tileTypes[1].getMovementTable()).isEqualTo(new MovementTable(Map.of(
                new UnitTag("Treaded"), 1.0,
                new UnitTag("Wheeled"), 1.0,
                new UnitTag("Soldier"), 0.75,
                new UnitTag("Air"), 1.0
        )));
        assertThat(tileTypes[1].getCover()).isZero();
        assertThat(tileTypes[4].getTags()).contains(new TileTag("HidingSpot"));
        assertThat(unitTypes[0].getName()).isEqualTo(new Name("Engineer"));
        assertThat(unitTypes[0].getAbilities()).first().hasToString("Build");
        assertThat(unitTypes[0].getAbilities()).last().hasToString("Repair");
        assertThat(unitTypes[11].getName()).isEqualTo(new Name("Ornithopter"));
        assertThat(unitTypes[11].getTags()).contains(new UnitTag("Air"));
        assertThat(unitTypes[11].getSpeed()).isEqualTo(6);
        assertThat(unitTypes[11].getVision()).isEqualTo(4);
        assertThat(unitTypes[11].getSupplyCost()).isEqualTo(300);
        assertThat(unitTypes[11].getAetherCost()).isEqualTo(20);
        assertThat(unitTypes[11].getHangar()).isEqualTo(new Hangar(Set.of(new UnitTag("Soldier"))));
        assertThat(unitTypes[16].getName()).isEqualTo(new Name("Corvette"));
        assertThat(unitTypes[16].getTags()).contains(new UnitTag("Naval")).contains(new UnitTag("Ship"));
        assertThat(unitTypes[16].getSpeed()).isEqualTo(6);
        assertThat(unitTypes[16].getVision()).isEqualTo(2);
        assertThat(unitTypes[16].getSupplyCost()).isEqualTo(600);
        assertThat(unitTypes[16].getAetherCost()).isEqualTo(60);
        assertThat(unitTypes[16].getWeapons()).hasSize(2).first().satisfies(weapon -> {
            assertThat(weapon.getName()).isEqualTo(new Name("Cannon"));
            assertThat(weapon).hasToString("Cannon");
            assertThat(weapon.getTargets()).containsExactly(new TargetSpec.AssetTargetSpec(
                    IFF.FOE, 0, 2
            ));
            assertThat(weapon.getDamage())
                    .containsEntry(unitTypes[0], 70)
                    .containsEntry(unitTypes[2], 50)
                    .containsEntry(new StructureTag("Structure"), 20);
        });
        assertThat(unitTypes[16].getWeapons()).last().satisfies(weapon -> {
            assertThat(weapon.getName()).isEqualTo(new Name("MachineGun"));
            assertThat(weapon).hasToString("MachineGun");
            assertThat(weapon.getDamage()).containsEntry(unitTypes[11], 60).containsEntry(unitTypes[12], 50);
            assertThat(weapon.getTargets()).containsExactly(new TargetSpec.UnitTargetSpec(
                    IFF.FOE, 0, 1
            ));
        });
    }

    @Test
    void load_WhenTheRulesetDoesNotExist_ThenReturnsNull() {
        var service = new RulesetServiceImpl("testRuleset", new ObjectMapper());
        assertThat(service.load(new Version("4.1.3"))).isNull();
    }

    @Test
    void load_WhenTheRulesetIsMalformed_ThenThrows() {
        var service = new RulesetServiceImpl("invalidRuleset", new ObjectMapper());
        assertThatThrownBy(() -> service.load(new Version("1.0.0-Empty")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed loading ruleset for version 1.0.0-Empty.")
                .hasCauseInstanceOf(JsonProcessingException.class);
    }

    @SafeVarargs
    private static <T> void validateIds(IntFunction<?> intToId, Function<T, ?> instanceToId, T... instances) {
        for (var i = 0; i < instances.length; i++) {
            assertThat(instanceToId.apply(instances[i])).isEqualTo(intToId.apply(i));
        }
    }

}
