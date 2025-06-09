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
        var commanders = ruleset.commanders().toArray(Commander[]::new);
        assertThat(commanders).hasSize(2);
        validateIds(CommanderId::new, Commander::id, commanders);
        var spells = ruleset.spells().toArray(Spell[]::new);
        assertThat(spells).hasSize(8);
        validateIds(SpellId::new, Spell::id, spells);
        var structureTypes = ruleset.structureTypes().toArray(StructureType[]::new);
        assertThat(structureTypes).hasSize(6);
        validateIds(StructureTypeId::new, StructureType::id, structureTypes);
        var tileTypes = ruleset.tileTypes().toArray(TileType[]::new);
        assertThat(tileTypes).hasSize(17);
        validateIds(TileTypeId::new, TileType::id, tileTypes);
        var unitTypes = ruleset.unitTypes().toArray(UnitType[]::new);
        validateIds(UnitTypeId::new, UnitType::id, unitTypes);
        assertThat(unitTypes).hasSize(19);
        assertThat(commanders[0].name()).isEqualTo(new Name("Alice"));
        assertThat(commanders[0].signatureSpells()).containsExactly(spells[0]);
        assertThat(commanders[0].affinities()).hasSize(2)
                .containsEntry(new SpellTag("Offensive"), Affinity.POSITIVE)
                .containsEntry(new SpellTag("Defensive"), Affinity.NEGATIVE);
        assertThat(commanders[1].name()).isEqualTo(new Name("Bob"));
        assertThat(commanders[1].signatureSpells()).containsExactly(spells[1]);
        assertThat(commanders[1].affinities()).hasSize(2)
                .containsEntry(new SpellTag("Defensive"), Affinity.POSITIVE)
                .containsEntry(new SpellTag("Offensive"), Affinity.NEGATIVE);
        assertThat(spells[0].name()).isEqualTo(new Name("MagicMissile"));
        assertThat(spells[0].targets()).containsExactly(new TargetSpec.UnitTargetSpec(IFF.FOE, 0, 0));
        assertThat(spells[0].focusCost()).isEqualTo(4000);
        assertThat(spells[0].tags()).contains(new SpellTag("Offensive"));
        assertThat(spells[1].name()).isEqualTo(new Name("QuickFix"));
        assertThat(spells[1].targets()).containsExactly(new TargetSpec.TileTargetSpec(true, 0, 0));
        assertThat(spells[1].focusCost()).isEqualTo(2400);
        assertThat(spells[1].tags()).contains(new SpellTag("Defensive"));
        assertThat(spells[2].name()).isEqualTo(new Name("HammerForce"));
        assertThat(spells[2].targets()).isEmpty();
        assertThat(spells[2].focusCost()).isEqualTo(1600);
        assertThat(spells[2].tags()).contains(new SpellTag("Offensive"));
        assertThat(structureTypes[0].name()).isEqualTo(new Name("Headquarters"));
        assertThat(structureTypes[0].tags()).contains(new StructureTag("Structure"));
        assertThat(structureTypes[0].foundation()).isEqualTo(tileTypes[0]);
        assertThat(structureTypes[0].supplyIncome()).isEqualTo(100);
        assertThat(structureTypes[0].supplyCost()).isEqualTo(1000);
        assertThat(structureTypes[0].buildTime()).isEqualTo(10000);
        assertThat(structureTypes[0].vision()).isEqualTo(2);
        assertThat(structureTypes[0].movementTable()).isEqualTo(new MovementTable(Map.of(
                new UnitTag("Land"), 1.0,
                new UnitTag("Air"), 1.0
        )));
        assertThat(structureTypes[0].cover()).isEqualTo(0.4);
        assertThat(structureTypes[3].name()).isEqualTo(new Name("Fort"));
        assertThat(structureTypes[3].tags()).contains(new StructureTag("Structure"));
        assertThat(structureTypes[3].foundation()).isEqualTo(tileTypes[13]);
        assertThat(structureTypes[3].supplyIncome()).isZero();
        assertThat(structureTypes[3].supplyCost()).isEqualTo(300);
        assertThat(structureTypes[3].buildTime()).isEqualTo(200);
        assertThat(structureTypes[3].vision()).isZero();
        assertThat(structureTypes[3].movementTable()).isEqualTo(new MovementTable(Map.of(
                new UnitTag("Land"), 1.0,
                new UnitTag("Air"), 1.0
        )));
        assertThat(structureTypes[3].deploymentRoster()).containsExactly(
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
        assertThat(structureTypes[3].cover()).isEqualTo(0.3);
        assertThat(tileTypes[0].name()).isEqualTo(new Name("Plains"));
        assertThat(tileTypes[0].tags()).isEmpty();
        assertThat(tileTypes[0].movementTable()).isEqualTo(new MovementTable(Map.of(
                new UnitTag("Treaded"), 1.0,
                new UnitTag("Wheeled"), 1.5,
                new UnitTag("Soldier"), 1.0,
                new UnitTag("Air"), 1.0
        )));
        assertThat(tileTypes[0].cover()).isEqualTo(0.1);
        assertThat(tileTypes[1].name()).isEqualTo(new Name("Road"));
        assertThat(tileTypes[1].tags()).isEmpty();
        assertThat(tileTypes[1].movementTable()).isEqualTo(new MovementTable(Map.of(
                new UnitTag("Treaded"), 1.0,
                new UnitTag("Wheeled"), 1.0,
                new UnitTag("Soldier"), 0.75,
                new UnitTag("Air"), 1.0
        )));
        assertThat(tileTypes[1].cover()).isZero();
        assertThat(tileTypes[4].tags()).contains(new TileTag("HidingSpot"));
        assertThat(unitTypes[0].name()).isEqualTo(new Name("Engineer"));
        assertThat(unitTypes[0].abilities()).first().hasToString("Build");
        assertThat(unitTypes[0].abilities()).last().hasToString("Repair");
        assertThat(unitTypes[11].name()).isEqualTo(new Name("Ornithopter"));
        assertThat(unitTypes[11].tags()).contains(new UnitTag("Air"));
        assertThat(unitTypes[11].speed()).isEqualTo(6);
        assertThat(unitTypes[11].vision()).isEqualTo(4);
        assertThat(unitTypes[11].supplyCost()).isEqualTo(300);
        assertThat(unitTypes[11].aetherCost()).isEqualTo(20);
        assertThat(unitTypes[11].hangar()).isEqualTo(new Hangar(Set.of(new UnitTag("Soldier"))));
        assertThat(unitTypes[16].name()).isEqualTo(new Name("Corvette"));
        assertThat(unitTypes[16].tags()).contains(new UnitTag("Naval")).contains(new UnitTag("Ship"));
        assertThat(unitTypes[16].speed()).isEqualTo(6);
        assertThat(unitTypes[16].vision()).isEqualTo(2);
        assertThat(unitTypes[16].supplyCost()).isEqualTo(600);
        assertThat(unitTypes[16].aetherCost()).isEqualTo(60);
        assertThat(unitTypes[16].weapons()).hasSize(2).first().satisfies(weapon -> {
            assertThat(weapon.name()).isEqualTo(new Name("Cannon"));
            assertThat(weapon).hasToString("Cannon");
            assertThat(weapon.targets()).containsExactly(new TargetSpec.AssetTargetSpec(
                    IFF.FOE, 0, 2
            ));
            assertThat(weapon.damage())
                    .containsEntry(unitTypes[0], 70)
                    .containsEntry(unitTypes[2], 50)
                    .containsEntry(new StructureTag("Structure"), 20);
        });
        assertThat(unitTypes[16].weapons()).last().satisfies(weapon -> {
            assertThat(weapon.name()).isEqualTo(new Name("MachineGun"));
            assertThat(weapon).hasToString("MachineGun");
            assertThat(weapon.damage()).containsEntry(unitTypes[11], 60).containsEntry(unitTypes[12], 50);
            assertThat(weapon.targets()).containsExactly(new TargetSpec.UnitTargetSpec(
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
