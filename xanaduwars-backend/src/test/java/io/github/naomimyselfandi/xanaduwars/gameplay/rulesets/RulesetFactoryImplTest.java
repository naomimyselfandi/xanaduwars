package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Affinity;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.UnitStat;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.UnitStatRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types.PassiveSpellType;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class RulesetFactoryImplTest {

    private Ruleset fixture;

    @BeforeEach
    void setup() throws IOException {
        var rulesetData = new RulesetData(
                new Manifest(
                        List.of("TerrainVisionBuffs"),
                        List.of("Sonja"),
                        List.of("EnhancedVision"),
                        List.of("Plains", "Mountain"),
                        List.of("RifleSquad", "Vanguard")
                ),
                Map.of(
                        "TerrainVisionBuffs", json("""
                        [
                          {
                            "rule": ".UnitStatRule",
                            "subjectFilter": "tile.name.Mountain",
                            "stat": "VISION",
                            "amount": 1,
                            "$comment": "In reality, this would be embedded in Mountain."
                          }
                        ]
                        """),
                        "Sonja", json("""
                        {
                          "tags": ["YellowComet"],
                          "signatureSpells": ["EnhancedVision"],
                          "affinities": {
                              "Fog": "POSITIVE",
                              "Luck": "NEGATIVE"
                          }
                        }
                        """),
                        "EnhancedVision", json("""
                        {
                          "spellType": ".PassiveSpellType",
                          "tags": ["Fog"],
                          "subjectRules": [{
                            "rule": ".UnitStatRule",
                            "stat": "VISION",
                            "amount": 1
                          }],
                          "focusCost": 300
                        }
                        """),
                        "Plains", json("""
                        {
                          "movementTable": {
                            "Soldier": 1,
                            "Wheeled": 1.5,
                            "Treaded": 1
                          },
                          "cover": 0.9
                        }
                        """),
                        "Mountain", json("""
                        {
                          "movementTable": {"Soldier": 2},
                          "cover": 0.4
                        }
                        """),
                        "RifleSquad", json("""
                        {
                          "tags": ["Soldier", "Land"],
                          "costs": {"SUPPLIES": 100},
                          "vision": 2,
                          "speed": 3,
                          "damageTable": {
                            "RifleSquad": 0.55,
                            "Vanguard": 0.05
                          }
                        }
                        """),
                        "Vanguard", json("""
                        {
                          "tags": ["Treaded", "Vehicle", "Land"],
                          "costs": {"SUPPLIES": 700},
                          "vision": 3,
                          "speed": 6,
                          "damageTable": {
                            "Vanguard": 0.55,
                            "RifleSquad": 0.70
                          }
                        }
                        """),
                        "details", json("""
                        {
                          "moveAction": {"name": "Move"},
                          "dropAction": {"name": "Drop"},
                          "attackAction": {"name": "Attack"},
                          "waitAction": {"name": "Wait"},
                          "deployAction": {"name": "Deploy"},
                          "passAction": {"name": "Pass"},
                          "resignAction": {"name": "Resign"}
                        }
                        """)
                )
        );
        fixture = new RulesetFactoryImpl(new ObjectMapper()).load(new Version("1.0.0"), rulesetData);
    }

    @Test
    void details() {
        assertThat(fixture.details()).satisfies(details -> {
            assertThat(details.dropAction()).isEqualTo(new DropAction(new Name("Drop")));
            assertThat(details.waitAction()).isEqualTo(new WaitAction(new Name("Wait")));
            assertThat(details.passAction()).isEqualTo(new PassAction(new Name("Pass")));
        });
    }

    @Test
    void globalRules() {
        assertThat(fixture.globalRules()).singleElement().isInstanceOfSatisfying(UnitStatRule.class, rule -> {
            assertThat(rule.subjectFilter()).hasToString("tile.name.Mountain");
            assertThat(rule.stat()).isEqualTo(UnitStat.VISION);
            assertThat(rule.amount()).isOne();
        });
    }

    @Test
    void commanderTypes() {
        assertThat(fixture.commanders()).singleElement().satisfies(commander -> {
            assertThat(commander.id()).isEqualTo(new CommanderId(0));
            assertThat(commander.name()).hasToString("Sonja");
            assertThatCollection(commander.tags()).containsExactly(new Tag("YellowComet"));
            assertThat(commander.affinities()).containsOnly(
                    Map.entry(new Tag("Fog"), Affinity.POSITIVE),
                    Map.entry(new Tag("Luck"), Affinity.NEGATIVE)
            );
            assertThat(commander.signatureSpells()).singleElement().isSameAs(fixture.spellTypes().getFirst());
        });
    }

    @Test
    void spellTypes() {
        assertThat(fixture.spellTypes())
                .singleElement()
                .isInstanceOfSatisfying(PassiveSpellType.class, spellType -> {
                    assertThat(spellType.id()).isEqualTo(new SpellTypeId(0));
                    assertThat(spellType.name()).hasToString("EnhancedVision");
                    assertThatCollection(spellType.tags()).containsExactly(new Tag("Fog"));
                    assertThat(spellType.subjectRules())
                            .singleElement()
                            .isInstanceOfSatisfying(UnitStatRule.class, rule -> {
                                assertThat(rule.subjectFilter()).hasToString("*");
                                assertThat(rule.stat()).isEqualTo(UnitStat.VISION);
                                assertThat(rule.amount()).isOne();
                            });
                });
    }

    @Test
    void unitTypesTypes() {
        var unitTypes = fixture.unitTypes();
        var rifleSquad = unitTypes.getFirst();
        var vanguard = unitTypes.getLast();
        assertThat(rifleSquad.id()).isEqualTo(new UnitTypeId(0));
        assertThat(rifleSquad.name()).hasToString("RifleSquad");
        assertThatCollection(rifleSquad.tags()).containsOnly(new Tag("Soldier"), new Tag("Land"));
        assertThat(rifleSquad.costs()).hasSize(1).containsEntry(Resource.SUPPLIES, 100);
        assertThat(rifleSquad.vision()).isEqualTo(2);
        assertThat(rifleSquad.speed()).isEqualTo(3);
        assertThat(rifleSquad.range()).isEqualTo(Range.MELEE);
        assertThat(rifleSquad.damageTable())
                .hasSize(2)
                .containsEntry(rifleSquad, Percent.withDoubleValue(0.55))
                .containsEntry(vanguard, Percent.withDoubleValue(0.05));
        assertThat(vanguard.id()).isEqualTo(new UnitTypeId(1));
    }

    private static String json(@Language("json") String json) {
        return json;
    }

}
