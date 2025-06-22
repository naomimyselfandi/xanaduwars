package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.FixedCost;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import jakarta.validation.Validation;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class RulesetServiceImplTest {

    private Ruleset ruleset;

    @BeforeEach
    void setup() {
        ruleset = new RulesetServiceImpl("testRuleset", new ObjectMapper()).load(new Version("1.2.3"));
    }

    @Test
    void validation() {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var violations = validatorFactory.getValidator().validate(ruleset);
            for (var violation : violations) System.err.println(violation); // Readability
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void naming() {
        for (var entry : ruleset.getScriptConstants().entrySet()) {
            assertThat(entry.getValue()).hasToString(entry.getKey());
        }
    }

    @Test
    void rules() {
        assertThat(ruleset.getRules()).first().returns(new Name("Rout"), Rule::getName);
    }

    @Test
    void commanders() {
        assertThat($("Alice", Commander.class))
                .isEqualTo(ruleset.getCommander(new CommanderId(0)))
                .returns(new Name("Alice"), Commander::getName)
                .returns(new CommanderId(0), Commander::getId)
                .returns(Set.of(), Commander::getTags)
                .returns(Map.of(
                        $("Offensive"), $("Positive"),
                        $("Defensive"), $("Negative")
                ), Commander::getAffinities)
                .returns(List.of($("MagicMissile", Spell.class)), Commander::getSignatureSpells)
                .returns(2, Commander::getChosenSpells);
        assertThat($("Bob", Commander.class))
                .isEqualTo(ruleset.getCommander(new CommanderId(1)))
                .returns(new Name("Bob"), Commander::getName)
                .returns(new CommanderId(1), Commander::getId)
                .returns(Set.of(), Commander::getTags)
                .returns(Map.of(
                        $("Offensive"), $("Negative"),
                        $("Defensive"), $("Positive")
                ), Commander::getAffinities)
                .returns(List.of($("QuickFix", Spell.class)), Commander::getSignatureSpells)
                .returns(2, Commander::getChosenSpells);
    }

    @Test
    void spells() {
        assertThat($("MagicMissile", Spell.class))
                .isEqualTo(ruleset.getSpell(new SpellId(0)))
                .returns(new Name("MagicMissile"), Spell::getName)
                .returns(new SpellId(0), Spell::getId)
                .returns(Set.of($("Offensive", SpellTag.class)), Spell::getTags)
                .returns(List.of(TargetSpec.builder()
                        .filters(Map.of(Kind.UNIT, true, Iff.ENEMY, true))
                        .build()
                ), Spell::getTargets)
                .returns(new FixedCost(0), Spell::getSupplyCost)
                .returns(new FixedCost(0), Spell::getAetherCost)
                .returns(new FixedCost(4000), Spell::getFocusCost);
        assertThat($("QuickFix", Spell.class))
                .isEqualTo(ruleset.getSpell(new SpellId(1)))
                .returns(new Name("QuickFix"), Spell::getName)
                .returns(new SpellId(1), Spell::getId)
                .returns(Set.of($("Defensive", SpellTag.class)), Spell::getTags)
                .returns(List.of(TargetSpec.builder().filters(Map.of(Kind.TILE, true)).build()), Spell::getTargets)
                .returns(FixedCost.ZERO, Spell::getSupplyCost)
                .returns(FixedCost.ZERO, Spell::getAetherCost)
                .returns(new FixedCost(2400), Spell::getFocusCost);
        assertThat($("HammerForce", Spell.class))
                .isEqualTo(ruleset.getSpell(new SpellId(2)))
                .returns(new Name("HammerForce"), Spell::getName)
                .returns(new SpellId(2), Spell::getId)
                .returns(Set.of($("Offensive", SpellTag.class)), Spell::getTags)
                .returns(List.of(), Spell::getTargets)
                .returns(FixedCost.ZERO, Spell::getSupplyCost)
                .returns(FixedCost.ZERO, Spell::getAetherCost)
                .returns(new FixedCost(1600), Spell::getFocusCost);
    }

    @Test
    void structureTypes() {
        assertThat($("Airfield", StructureType.class))
                .isEqualTo(ruleset.getStructureType(new StructureTypeId(5)))
                .returns(new Name("Airfield"), StructureType::getName)
                .returns(new StructureTypeId(5), StructureType::getId)
                .returns(Set.of($("Building", StructureTag.class)), StructureType::getTags)
                .returns(Map.of(
                        $("Land"), 1.0,
                        $("Air"), 1.0
                ), StructureType::getMovementTable)
                .returns(0.3, StructureType::getCover)
                .returns(List.of($("Ornithopter"), $("Biplane"), $("Airship")),
                        StructureType::getActions)
                .returns(new FixedCost(300), StructureType::getSupplyCost)
                .returns(new FixedCost(200), StructureType::getAetherCost)
                .returns(new FixedCost(0), StructureType::getFocusCost)
                .returns(List.of(), StructureType::getTargets)
        ;
    }

    @Test
    void tileTypes() {
        assertThat($("Plains", TileType.class))
                .isEqualTo(ruleset.getTileType(new TileTypeId(0)))
                .returns(new Name("Plains"), TileType::getName)
                .returns(new TileTypeId(0), TileType::getId)
                .returns(Set.of(), TileType::getTags)
                .returns(Map.of(
                        $("Treaded"), 1.0,
                        $("Wheeled"), 1.5,
                        $("Soldier"), 1.0,
                        $("Air"), 1.0
                ), TileType::getMovementTable)
                .returns(0.1, TileType::getCover);
        assertThat($("Ruins", TileType.class))
                .isEqualTo(ruleset.getTileType(new TileTypeId(4)))
                .returns(new Name("Ruins"), TileType::getName)
                .returns(new TileTypeId(4), TileType::getId)
                .returns(Set.of($("HidingSpot", TileTag.class)), TileType::getTags)
                .returns(Map.of(
                        $("Treaded"), 1.0,
                        $("Wheeled"), 1.0,
                        $("Soldier"), 1.0,
                        $("Air"), 1.0
                ), TileType::getMovementTable)
                .returns(0.2, TileType::getCover);
    }

    @Test
    void unitTypes() {
        assertThat($("Engineer", UnitType.class))
                .isEqualTo(ruleset.getUnitType(new UnitTypeId(0)))
                .returns(new Name("Engineer"), UnitType::getName)
                .returns(new UnitTypeId(0), UnitType::getId)
                .returns(Set.of($("Soldier"), $("Land")), UnitType::getTags)
                .returns(4, UnitType::getSpeed)
                .returns(2, UnitType::getVision)
                .returns(new FixedCost(100), UnitType::getSupplyCost)
                .returns(new FixedCost(0), UnitType::getAetherCost)
                .returns(List.of(), Action::getTargets)
                .returns(Script.NULL, Action::getPrecondition)
                .returns(Script.NULL, Action::getValidation)
                .returns(List.of(
                        $("RepairStructure"),
                        $("Depot"),
                        $("Tower"),
                        $("Fort"),
                        $("Port"),
                        $("Airfield")
                ), UnitType::getActions)
                .extracting(UnitType::getWeapons)
                .asInstanceOf(InstanceOfAssertFactories.list(Weapon.class))
                .singleElement()
                .satisfies(weapon -> {
                    assertThat(weapon.getName()).isEqualTo(new Name("Rifle"));
                    assertThat(weapon).hasToString("Rifle");
                    assertThat(weapon.getDamage())
                            .containsEntry($("Engineer"), 40)
                            .containsEntry($("RifleSquad"), 20);
                    assertThat(weapon.getTargets()).containsOnly(TargetSpec
                            .builder()
                            .filters(Map.of(Kind.STRUCTURE, true, Kind.UNIT, true, Iff.ENEMY, true, Iff.NEUTRAL, true))
                            .build());
                });
        assertThat($("RifleSquad", UnitType.class))
                .isEqualTo(ruleset.getUnitType(new UnitTypeId(1)))
                .returns(new UnitTypeId(1), UnitType::getId)
                .extracting(UnitType::getWeapons)
                .extracting(List::getFirst)
                .extracting(Weapon::getDamage)
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsEntry($("Engineer"), 60)
                .containsEntry($("RifleSquad"), 50)
                .containsEntry($("RepeaterSquad"), 20);
        var weapons = assertThat($("Corvette", UnitType.class))
                .isEqualTo(ruleset.getUnitType(new UnitTypeId(16)))
                .returns(new Name("Corvette"), UnitType::getName)
                .returns(new UnitTypeId(16), UnitType::getId)
                .returns(Set.of($("Naval"), $("Ship")), UnitType::getTags)
                .returns(6, UnitType::getSpeed)
                .returns(2, UnitType::getVision)
                .returns(new FixedCost(600), UnitType::getSupplyCost)
                .returns(new FixedCost(60), UnitType::getAetherCost)
                .returns(List.of(), UnitType::getActions)
                .extracting(UnitType::getWeapons)
                .asInstanceOf(InstanceOfAssertFactories.list(Weapon.class))
                .hasSize(2);
        weapons.first().satisfies(weapon -> {
            assertThat(weapon.getName()).isEqualTo(new Name("Cannon"));
            assertThat(weapon).hasToString("Cannon");
            assertThat(weapon.getDamage())
                    .containsEntry($("Engineer"), 70)
                    .containsEntry($("RifleSquad"), 70)
                    .containsEntry($("Vanguard"), 60);
            assertThat(weapon.getTargets()).containsOnly(TargetSpec
                    .builder()
                    .filters(Map.of(Kind.STRUCTURE, true, Kind.UNIT, true, Iff.ENEMY, true, Iff.NEUTRAL, true))
                    .minRange(0)
                    .maxRange(2)
                    .build());
        });
        weapons.last().satisfies(weapon -> {
            assertThat(weapon.getName()).isEqualTo(new Name("MachineGun"));
            assertThat(weapon).hasToString("MachineGun");
            assertThat(weapon.getDamage())
                    .containsEntry($("Ornithopter"), 60)
                    .containsEntry($("Biplane"), 50)
                    .containsEntry($("Airship"), 40);
            assertThat(weapon.getTargets()).containsOnly(TargetSpec
                    .builder()
                    .filters(Map.of(Kind.STRUCTURE, true, Kind.UNIT, true, Iff.ENEMY, true, Iff.NEUTRAL, true))
                    .minRange(0)
                    .maxRange(1)
                    .build());
        });
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private <T> T $(String name, T... reified) {
        return $(name, (Class<T>) reified.getClass().getComponentType());
    }

    private <T> T $(String name, Class<T> type) {
        var result = ruleset.getScriptConstants().get(name);
        if (result == null) {
            return fail("Cannot resolve '%s'.", name);
        } else if (type.isInstance(result)) {
            return type.cast(result);
        } else {
            var actual = result.getClass().getSimpleName();
            var expected = type.getSimpleName();
            return fail("'%s' is a(n) %s, not a(n) %s.", name, actual, expected);
        }
    }

}
