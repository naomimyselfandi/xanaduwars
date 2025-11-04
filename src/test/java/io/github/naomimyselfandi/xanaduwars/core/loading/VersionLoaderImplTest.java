package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.messages.SimpleMessageType;
import io.github.naomimyselfandi.xanaduwars.core.messages.UnitCreatedEvent;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.message.*;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.JsonAutoSubTypes;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(SeededRandomExtension.class)
class VersionLoaderImplTest {

    private VersionLoaderImpl fixture;

    private VersionImpl version;

    @BeforeEach
    void setup() {
        var objectMapper = new ObjectMapper().enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature());
        JsonAutoSubTypes.SUPPORT.accept(objectMapper);
        fixture = new VersionLoaderImpl(objectMapper);
    }

    @Test
    void load(SeededRng random) throws IOException {
        @Language("json") var json = """
                {
                  "eventTypes": [
                    {"name": "FooEvent", "properties": ["lhs", "rhs"]}
                  ],
                  "queryTypes": [
                    {"name": "BarQuery", "properties": ["a", "b", "c"], "defaultValue": "return(a + b)"}
                  ],
                  "globalRules": [
                    {
                      "on": "FooEvent",
                      "then": "doSomething()"
                    },
                    {
                      "on": "BarQuery",
                      "when": "return(c > 0)",
                      "then": "return(value + 42)"
                    },
                    {
                      "on": "UnitCreated",
                      "then": "unit.sayHello()"
                    }
                  ],
                  "libraries": {
                    "Util": [
                      "def sum(lhs, rhs):",
                      "  return(lhs + rhs)",
                      "end",
                      "def mul(lhs, rhs):",
                      "  return(lhs * rhs)",
                      "end"
                    ]
                  },
                  "actions": [
                    {
                      "name": "StimPack",
                      "effect": "Testing.doStimPack(actor) // fake implementation for simplicity"
                    }
                  ],
                  "spells": [
                    {
                      "name": "MagicMissile",
                      "tags": ["Damage"],
                      "focusCost": 1800,
                      "target": "unit.enemy",
                      "effect": "target.hp = target.hp - 30"
                    },
                    {
                      "name": "HealingWave",
                      "tags": ["Healing"],
                      "focusCost": 900,
                      "filter": [
                        "#units = actor.units.iterator",
                        "label loop:",
                        "  units.hasNext() || return(false)",
                        "  #unit = units.next()",
                        "  (unit.hp < unit.maxHp) && return(true)",
                        "  goto(loop)"
                      ],
                      "effect": [
                        "#units = actor.units.iterator",
                        "label loop:",
                        "  units.hasNext() || return()",
                        "  #unit = units.next",
                        "  unit.hp = unit.hp + 20",
                        "  goto(loop)"
                      ]
                    }
                  ],
                  "abilityTags": ["Damage", "Healing", "Movement"],
                  "buildTarget": "tile[1,1]",
                  "buildFilter": "Testing.doBuildFilter() // fake implementation for simplicity",
                  "buildEffect": "Testing.doBuildEffect() // fake implementation for simplicity",
                  "moveAbility": {
                    "name": "Move"
                  },
                  "fireAbility": {
                    "name": "Fire",
                    "target": "unit.enemy",
                    "filter": "Testing.doFireFilter()",
                    "effect": "Testing.doFireEffect()"
                  },
                  "dropAbility": {
                    "name": "Drop",
                    "target": "tile[1,1]",
                    "filter": "Testing.doDropFilter()",
                    "effect": "Testing.doDropEffect()"
                  },
                  "commanders": [
                    {
                      "name": "Alice",
                      "signatureSpells": ["MagicMissile"],
                      "positiveAffinities": ["Damage"],
                      "negativeAffinities": ["Healing"]
                    }
                  ],
                  "tileTags": ["Urban", "Water"],
                  "tileTypes": [
                    {
                      "name": "Road",
                      "movementTable": {"Soldier": 1, "Vehicle": 1},
                      "tags": ["Urban"]
                    },
                    {
                      "name": "Plains",
                      "movementTable": {"Soldier": 1, "Vehicle": 1.5},
                      "cover": 0.1
                    },
                    {
                      "name": "River",
                      "movementTable": {"Soldier": 2},
                      "tags": ["Water"]
                    }
                  ],
                  "unitTags": ["Soldier", "Vehicle", "Structure"],
                  "unitTypes": [
                    {
                      "name": "Grunt",
                      "tags": ["Soldier"],
                      "speed": 3,
                      "perception": 2,
                      "maxHp": 100,
                      "supplyCost": 100,
                      "abilities": ["StimPack"],
                      "weapons": [
                        {
                          "name": "Rifle",
                          "damage": {"Soldier": 50, "Vehicle": 20}
                        }
                      ]
                    },
                    {
                      "name": "Tank",
                      "tags": ["Vehicle"],
                      "speed": 6,
                      "perception": 2,
                      "maxHp": 200,
                      "supplyCost": 300,
                      "aetherCost": 100,
                      "weapons": [
                        {
                          "name": "Cannon",
                          "damage": {
                            "Vehicle": 50,
                            "Soldier": 40,
                            "Structure": 50
                          },
                          "minRange": 2,
                          "maxRange": 3
                        }
                      ]
                    },
                    {
                      "name": "APC",
                      "tags": ["Vehicle"],
                      "speed": 6,
                      "perception": 1,
                      "maxHp": 150,
                      "supplyCost": 300,
                      "aetherCost": 50,
                      "hangar": "Soldier"
                    },
                    {
                      "name": "Base",
                      "tags": ["Structure"],
                      "speed": 0,
                      "perception": 2,
                      "maxHp": 300,
                      "supplyCost": 500,
                      "abilities": [
                        "BuildGrunt",
                        "BuildTank",
                        "BuildAPC"
                      ],
                      "omitBuildAbility": true
                    }
                  ],
                  "redactionPolicy": "Testing.doRedaction() // fake implementation for simplicity"
                }
                """;
        var versionNumber = random.<VersionNumber>get();
        version = fixture.load(versionNumber, json);
        assertThat(version.getVersionNumber()).isEqualTo(versionNumber);
        assertThat(version.lookup("Util")).isEqualTo(Script.of("""
                        def sum(lhs, rhs):
                          return(lhs + rhs)
                        end
                        def mul(lhs, rhs):
                          return(lhs * rhs)
                        end
                        """.lines().toList()));
        assertThat(version.lookup("Damage")).isEqualTo(new AbilityTag("Damage"));
        assertThat(version.lookup("Healing")).isEqualTo(new AbilityTag("Healing"));
        assertThat(version.lookup("Movement")).isEqualTo(new AbilityTag("Movement"));
        assertThat(version.lookup("Urban")).isEqualTo(new TileTag("Urban"));
        assertThat(version.lookup("Water")).isEqualTo(new TileTag("Water"));
        assertThat(version.lookup("Soldier")).isEqualTo(new UnitTag("Soldier"));
        assertThat(version.lookup("Vehicle")).isEqualTo(new UnitTag("Vehicle"));
        assertThat(version.lookup("Structure")).isEqualTo(new UnitTag("Structure"));
        assertThat(version.lookup("Alice")).isInstanceOfSatisfying(Commander.class, it -> {
            assertThat(it.getName()).isEqualTo("Alice");
            assertThat(it.getSignatureSpells()).isUnmodifiable().containsExactly($("MagicMissile"));
            assertThat(it.getPositiveAffinities()).isUnmodifiable().containsExactly($("Damage"));
            assertThat(it.getNegativeAffinities()).isUnmodifiable().containsExactly($("Healing"));
            assertThat(it.getChosenSpells()).isEqualTo(2);
        });
        assertThat(version.lookup("MagicMissile"))
                .isInstanceOfSatisfying(AbilityDeclaration.class, it -> {
                    assertThat(it.getName()).isEqualTo("MagicMissile");
                    assertThat(it.isSpellChoice()).isFalse();
                    assertThat(it.getTags()).isUnmodifiable().containsExactly($("Damage"));
                    assertThat(it.getFocusCost()).isEqualTo(Script.of(1800));
                    assertThat(it.getTarget()).isEqualTo(new TargetOfEnemyUnit(new TargetOfUnit(TargetOfTile.TILE)));
                    assertThat(it.getFilter()).isEqualTo(Script.TRUE);
                    assertThat(it.getEffect()).isEqualTo(Script.of("target.hp = target.hp - 30"));
                });
        assertThat(version.lookup("HealingWave"))
                .isInstanceOfSatisfying(AbilityDeclaration.class, it -> {
                    assertThat(it.getName()).isEqualTo("HealingWave");
                    assertThat(it.isSpellChoice()).isTrue();
                    assertThat(it.getTags()).isUnmodifiable().containsExactly($("Healing"));
                    assertThat(it.getFocusCost()).isEqualTo(Script.of(900));
                    assertThat(it.getTarget()).isEqualTo(TargetOfNothing.NOTHING);
                    assertThat(it.getFilter()).isEqualTo(Script.of("""
                            #units = actor.units.iterator
                            label loop:
                              units.hasNext() || return(false)
                              #unit = units.next()
                              (unit.hp < unit.maxHp) && return(true)
                              goto(loop)
                            """.lines().toList()));
                    assertThat(it.getEffect()).isEqualTo(Script.of("""
                    #units = actor.units.iterator
                        label loop:
                          units.hasNext() || return()
                          #unit = units.next
                          unit.hp = unit.hp + 20
                          goto(loop)
                    """.lines().toList()));
                });
        assertThat(version.lookup("StimPack"))
                .isInstanceOfSatisfying(AbilityDeclaration.class, it -> {
                    assertThat(it.getName()).isEqualTo("StimPack");
                    assertThat(it.isSpellChoice()).isFalse();
                    assertThat(it.getTags()).isUnmodifiable().isEmpty();
                    assertThat(it.getSupplyCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getAetherCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getFocusCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getTarget()).isEqualTo(TargetOfNothing.NOTHING);
                    assertThat(it.getFilter()).isEqualTo(Script.TRUE);
                    assertThat(it.getEffect()).isEqualTo(Script.of("Testing.doStimPack(actor)"));
                });
        assertThat(version.lookup("Move"))
                .isInstanceOfSatisfying(MovementAbility.class, it -> {
                    assertThat(it.getName()).isEqualTo("Move");
                    assertThat(it.getTags()).isUnmodifiable().isEmpty();
                });
        assertThat(version.getMoveAbility()).isEqualTo($("Move"));
        assertThat(version.lookup("Fire"))
                .isInstanceOfSatisfying(AbilityDeclaration.class, it -> {
                    assertThat(it.getName()).isEqualTo("Fire");
                    assertThat(it.getTags()).isUnmodifiable().isEmpty();
                    assertThat(it.getSupplyCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getAetherCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getFocusCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getTarget()).isEqualTo(new TargetOfEnemyUnit(new TargetOfUnit(TargetOfTile.TILE)));
                    assertThat(it.getFilter()).isEqualTo(Script.of("Testing.doFireFilter()"));
                    assertThat(it.getEffect()).isEqualTo(Script.of("Testing.doFireEffect()"));
                });
        assertThat(version.getFireAbility()).isSameAs($("Fire"));
        assertThat(version.lookup("Drop"))
                .isInstanceOfSatisfying(AbilityDeclaration.class, it -> {
                    assertThat(it.getName()).isEqualTo("Drop");
                    assertThat(it.getTags()).isUnmodifiable().isEmpty();
                    assertThat(it.getSupplyCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getAetherCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getFocusCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getTarget()).isEqualTo(new TargetOfRange<>(TargetOfTile.TILE, 1, 1));
                    assertThat(it.getFilter()).isEqualTo(Script.of("Testing.doDropFilter()"));
                    assertThat(it.getEffect()).isEqualTo(Script.of("Testing.doDropEffect()"));
                });
        assertThat(version.getDropAbility()).isSameAs($("Drop"));
        assertThat(version.lookup("Road")).isInstanceOfSatisfying(TileType.class, it -> {
            assertThat(it.getName()).isEqualTo("Road");
            assertThat(it.getTags()).isUnmodifiable().containsExactly($("Urban"));
            assertThat(it.getMovementTable()).isUnmodifiable().hasSize(2)
                    .containsEntry($("Soldier"), 1.0)
                    .containsEntry($("Vehicle"), 1.0);
        });
        assertThat(version.lookup("Plains")).isInstanceOfSatisfying(TileType.class, it -> {
            assertThat(it.getName()).isEqualTo("Plains");
            assertThat(it.getTags()).isUnmodifiable().isEmpty();
            assertThat(it.getMovementTable()).isUnmodifiable().hasSize(2)
                    .containsEntry($("Soldier"), 1.0)
                    .containsEntry($("Vehicle"), 1.5);
        });
        assertThat(version.lookup("River")).isInstanceOfSatisfying(TileType.class, it -> {
            assertThat(it.getName()).isEqualTo("River");
            assertThat(it.getTags()).isUnmodifiable();
            assertThat(it.getMovementTable()).isUnmodifiable().hasSize(1)
                    .containsEntry($("Soldier"), 2.0);
        });
        assertThat(version.lookup("Grunt")).isInstanceOfSatisfying(UnitType.class, it -> {
            assertThat(it.getName()).isEqualTo("Grunt");
            assertThat(it.getTags()).isUnmodifiable().containsExactly($("Soldier"));
            assertThat(it.getSpeed()).isEqualTo(3);
            assertThat(it.getPerception()).isEqualTo(2);
            assertThat(it.getMaxHp()).isEqualTo(100);
            assertThat(it.getSupplyCost()).isEqualTo(100);
            assertThat(it.getAetherCost()).isZero();
            assertThat(it.getAbilities()).isUnmodifiable().containsExactly($("StimPack"));
            assertThat(it.getWeapons()).containsExactly(new Weapon("Rifle", UnitSelectorMap.copyOf(Map.of(
                    $("Soldier"), 50,
                    $("Vehicle"), 20
            )), 1, 1));
            assertThat(it.getHangar()).isEqualTo(UnitSelectorPlaceholder.NONE);
        });
        assertThat(version.lookup("BuildGrunt"))
                .isInstanceOfSatisfying(BuildAbilityDeclaration.class, it -> {
                    assertThat(it.getName()).isEqualTo("BuildGrunt");
                    assertThat(it.getUnitType()).isEqualTo($("Grunt"));
                    assertThat(it.getSupplyCost()).isEqualTo(Script.of(100));
                    assertThat(it.getAetherCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getFocusCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getTarget()).isEqualTo(new TargetOfRange<>(TargetOfTile.TILE, 1, 1));
                    assertThat(it.getFilter()).isEqualTo(Script.of("Testing.doBuildFilter()"));
                    assertThat(it.getEffect()).isEqualTo(Script.of("Testing.doBuildEffect()"));
                });
        assertThat(version.lookup("APC")).isInstanceOfSatisfying(UnitType.class, it -> {
            assertThat(it.getName()).isEqualTo("APC");
            assertThat(it.getTags()).isUnmodifiable().containsExactly($("Vehicle"));
            assertThat(it.getSpeed()).isEqualTo(6);
            assertThat(it.getPerception()).isEqualTo(1);
            assertThat(it.getMaxHp()).isEqualTo(150);
            assertThat(it.getSupplyCost()).isEqualTo(300);
            assertThat(it.getAetherCost()).isEqualTo(50);
            assertThat(it.getAbilities()).isUnmodifiable().isEmpty();
            assertThat(it.getHangar()).isEqualTo($("Soldier"));
        });
        assertThat(version.lookup("BuildAPC"))
                .isInstanceOfSatisfying(BuildAbilityDeclaration.class, it -> {
                    assertThat(it.getName()).isEqualTo("BuildAPC");
                    assertThat(it.getUnitType()).isEqualTo($("APC"));
                    assertThat(it.getSupplyCost()).isEqualTo(Script.of(300));
                    assertThat(it.getAetherCost()).isEqualTo(Script.of(50));
                    assertThat(it.getFocusCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getTarget()).isEqualTo(new TargetOfRange<>(TargetOfTile.TILE, 1, 1));
                    assertThat(it.getFilter()).isEqualTo(Script.of("Testing.doBuildFilter()"));
                    assertThat(it.getEffect()).isEqualTo(Script.of("Testing.doBuildEffect()"));
                });
        assertThat(version.lookup("Tank")).isInstanceOfSatisfying(UnitType.class, it -> {
            assertThat(it.getName()).isEqualTo("Tank");
            assertThat(it.getTags()).isUnmodifiable().containsExactly($("Vehicle"));
            assertThat(it.getSpeed()).isEqualTo(6);
            assertThat(it.getPerception()).isEqualTo(2);
            assertThat(it.getMaxHp()).isEqualTo(200);
            assertThat(it.getSupplyCost()).isEqualTo(300);
            assertThat(it.getAetherCost()).isEqualTo(100);
            assertThat(it.getAbilities()).isUnmodifiable().isEmpty();
            assertThat(it.getWeapons()).containsExactly(new Weapon("Cannon", UnitSelectorMap.copyOf(Map.of(
                    $("Soldier"), 40,
                    $("Vehicle"), 50,
                    $("Structure"), 50
            )), 2, 3));
            assertThat(it.getHangar()).isEqualTo(UnitSelectorPlaceholder.NONE);
        });
        assertThat(version.lookup("BuildTank"))
                .isInstanceOfSatisfying(BuildAbilityDeclaration.class, it -> {
                    assertThat(it.getName()).isEqualTo("BuildTank");
                    assertThat(it.getUnitType()).isEqualTo($("Tank"));
                    assertThat(it.getSupplyCost()).isEqualTo(Script.of(300));
                    assertThat(it.getAetherCost()).isEqualTo(Script.of(100));
                    assertThat(it.getFocusCost()).isEqualTo(Script.ZERO);
                    assertThat(it.getTarget()).isEqualTo(new TargetOfRange<>(TargetOfTile.TILE, 1, 1));
                    assertThat(it.getFilter()).isEqualTo(Script.of("Testing.doBuildFilter()"));
                    assertThat(it.getEffect()).isEqualTo(Script.of("Testing.doBuildEffect()"));
                });
        assertThat(version.lookup("Base")).isInstanceOfSatisfying(UnitType.class, it -> {
            assertThat(it.getName()).isEqualTo("Base");
            assertThat(it.getTags()).isUnmodifiable().containsExactly($("Structure"));
            assertThat(it.getSpeed()).isEqualTo(0);
            assertThat(it.getPerception()).isEqualTo(2);
            assertThat(it.getMaxHp()).isEqualTo(300);
            assertThat(it.getSupplyCost()).isEqualTo(500);
            assertThat(it.getAetherCost()).isZero();
            assertThat(it.getAbilities()).isUnmodifiable().containsExactly(
                    $("BuildGrunt"),
                    $("BuildTank"),
                    $("BuildAPC")
            );
            assertThat(it.getHangar()).isEqualTo(UnitSelectorPlaceholder.NONE);
        });
        assertThat(version.lookup("BuildBase")).isNull();
        assertThat(version.getGlobalRules()).isUnmodifiable().containsExactly(
                new Rule($("FooEvent"), Script.TRUE, Script.of("doSomething()")),
                new Rule($("BarQuery"), Script.of("return(c > 0)"), Script.of("return(value + 42)")),
                new Rule(new SimpleMessageType(UnitCreatedEvent.class), Script.TRUE, Script.of("unit.sayHello()"))
        );
        assertThat(version.getRedactionPolicy()).isEqualTo(Script.of("Testing.doRedaction()"));
    }

    @MethodSource
    @ParameterizedTest
    void load_WhenTheInputIsInvalid_ThenThrows(String json, String message) {
        var versionNumber = new VersionNumber(1, 2, 3, "invalid");
        assertThatThrownBy(() -> fixture.load(versionNumber, json)).hasMessageContaining(message);
    }

    @SuppressWarnings("unchecked")
    private <T> T $(String name, T... typeHint) {
        var result = version.lookup(name);
        var component = typeHint.getClass().getComponentType();
        if (component.isArray()) {
            component = component.getComponentType();
            assertThat(result).isInstanceOf(component);
            var array = Array.newInstance(component, 1);
            Array.set(array, 0, result);
            return (T) array;
        } else {
            assertThat(result).isInstanceOf(component);
            return (T) result;
        }
    }

    private static Arguments invalid(@Language("json") String json, String message) {
        return arguments(json, message);
    }

    private static Stream<Arguments> load_WhenTheInputIsInvalid_ThenThrows() {
        return Stream.of(
                invalid("""
                        {
                          "globalRules": [],
                          "libraries": {},
                          "actions": [],
                          "spells": [],
                          "abilityTags": [],
                          "commanders": [],
                          "tileTags": ["Water"],
                          "tileTypes": [],
                          "unitTags": ["Water"],
                          "unitTypes": [],
                          "moveAbility": {
                            "name": "Move"
                          },
                          "fireAbility": {
                            "name": "Fire",
                            "target": "unit.enemy",
                            "filter": "Testing.doFireFilter()",
                            "effect": "Testing.doFireEffect()"
                          },
                          "dropAbility": {
                            "name": "Drop",
                            "target": "tile[1,1]",
                            "filter": "Testing.doDropFilter()",
                            "effect": "Testing.doDropEffect()"
                          }
                        }
                        """, "Got multiple declarations with the same name"
                ),
                invalid("""
                        {
                          "globalRules": [],
                          "libraries": {},
                          "actions": [],
                          "spells": [],
                          "abilityTags": [],
                          "commanders": [],
                          "tileTags": [],
                          "tileTypes": [],
                          "unitTags": [],
                          "unitTypes": [
                            {
                              "name": "SomeUnit",
                              "tags": ["SomeTag"]
                            }
                          ],
                          "moveAbility": {
                            "name": "Move"
                          },
                          "fireAbility": {
                            "name": "Fire",
                            "target": "unit.enemy",
                            "filter": "Testing.doFireFilter()",
                            "effect": "Testing.doFireEffect()"
                          },
                          "dropAbility": {
                            "name": "Drop",
                            "target": "tile[1,1]",
                            "filter": "Testing.doDropFilter()",
                            "effect": "Testing.doDropEffect()"
                          }
                        }
                        """, "Unknown declaration 'SomeTag'"
                ),
                invalid("""
                        {
                          "globalRules": [],
                          "libraries": {},
                          "actions": [],
                          "spells": [],
                          "abilityTags": [],
                          "commanders": [],
                          "tileTags": ["SomeTag"],
                          "tileTypes": [],
                          "unitTags": [],
                          "unitTypes": [
                            {
                              "name": "SomeUnit",
                              "tags": ["SomeTag"]
                            }
                          ],
                          "moveAbility": {
                            "name": "Move"
                          },
                          "fireAbility": {
                            "name": "Fire",
                            "target": "unit.enemy",
                            "filter": "Testing.doFireFilter()",
                            "effect": "Testing.doFireEffect()"
                          },
                          "dropAbility": {
                            "name": "Drop",
                            "target": "tile[1,1]",
                            "filter": "Testing.doDropFilter()",
                            "effect": "Testing.doDropEffect()"
                          }
                        }
                        """, "Inappropriate declaration 'SomeTag'"
                )
        );
    }

}