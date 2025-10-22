package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.loading.DeclarationModule;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.service.Snapshot;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SnapshotServiceImplTest {

    private static final @Language("json") String JSON = """
            {
              "version": "1.2.3-test",
              "width": 4,
              "height": 3,
              "players": [
                {
                  "team": 1,
                  "supplies": 2,
                  "aether": 3,
                  "focus": 4,
                  "commander": "alice",
                  "abilities": ["psionicStorm", "hallucination", "disruptionWeb"],
                  "activeAbilities": ["psionicStorm"],
                  "usedAbilities": ["psionicStorm", "disruptionWeb"]
                },
                {
                  "team": 5,
                  "supplies": 6,
                  "aether": 7,
                  "focus": 8,
                  "commander": "bob",
                  "abilities": ["feedback", "mindControl", "maelstrom"],
                  "usedAbilities": ["mindControl"]
                }
              ],
              "tiles": [
                {
                  "type": "road",
                  "unit": {"type": "dragoon", "owner": 0, "hpPercent": 0.8, "activeAbilities": ["singularityCharge"]}
                },
                {
                  "type": "road",
                  "unit": {"type": "zealot", "owner": 0, "hpPercent": 0.6}
                },
                {"type": "ocean"},
                {"type": "ocean"},
                {"type": "road"},
                {"type": "plains"},
                {"type": "plains"},
                {"type": "road"},
                {"type": "ocean"},
                {"type": "ocean"},
                {
                  "type": "road",
                  "unit": {"type": "zealot", "owner": 1, "hpPercent": 0.6}
                },
                {
                  "type": "road",
                  "unit": {"type": "dragoon", "owner": 1, "hpPercent": 0.8}
                }
              ]
            }
            """;

    @Mock
    private Commander alice, bob;

    @Mock
    private Ability psionicStorm, hallucination, feedback, mindControl, maelstrom, disruptionWeb, singularityCharge;

    @Mock
    private TileType road, plains, ocean;

    @Mock
    private UnitType zealot, dragoon;

    @Mock
    private Version version;

    private SnapshotServiceImpl fixture;

    @BeforeEach
    void setup() throws ReflectiveOperationException {
        var declarations = new HashMap<>();
        for (var field : SnapshotServiceImplTest.class.getDeclaredFields()) {
            if (field.get(this) instanceof Specification declaration) {
                when(declaration.getName()).thenReturn(field.getName());
                declarations.put(field.getName(), declaration);
                if (declaration instanceof UnitType unitType) {
                    when(unitType.getMaxHp()).thenReturn(Integer.MAX_VALUE);
                }
            }
        }
        when(version.lookup(any())).then(i -> declarations.get(i.getArgument(0)));
        when(version.getVersionNumber()).thenReturn(new VersionNumber(1, 2, 3, "test"));
        var versionDeserializer = new StdDeserializer<Version>(Version.class) {

            @Override
            public Version deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                assertThat(parser.readValueAs(VersionNumber.class)).isEqualTo(version.getVersionNumber());
                return version;
            }

        };
        var module = new DeclarationModule(version).addDeserializer(Version.class, versionDeserializer);
        var objectMapper = new ObjectMapper().registerModule(module);
        fixture = new SnapshotServiceImpl(objectMapper);
    }

    @Test
    void load() {
        validate(fixture.load(new Snapshot(JSON)));
    }

    @Test
    void save() {
        var gameState = fixture.load(new Snapshot(JSON));
        var snapshot = fixture.save(gameState);
        validate(fixture.load(snapshot));
    }

    @SuppressWarnings("DataFlowIssue") // false positive
    private void validate(GameState gameState) {
        assertThat(gameState.getPlayers()).isUnmodifiable().hasSize(2).allSatisfy(it ->  {
            assertThat(it.getGameState()).isSameAs(gameState);
            assertThat(gameState.getPlayer(it.getPosition())).isSameAs(it);
        });
        assertThat(gameState.getPlayer(0)).satisfies(it -> {
            assertThat(it.getTeam()).isEqualTo(1);
            assertThat(it.getSupplies()).isEqualTo(2);
            assertThat(it.getAether()).isEqualTo(3);
            assertThat(it.getFocus()).isEqualTo(4);
            assertThat(it.getCommander()).isEqualTo(alice);
            assertThat(it.getAbilities()).containsExactly(psionicStorm, hallucination, disruptionWeb);
            assertThat(it.getActiveAbilities()).containsExactly(psionicStorm);
            assertThat(it.getUsedAbilities()).containsExactly(psionicStorm, disruptionWeb);
        });
        assertThat(gameState.getPlayer(1)).satisfies(it -> {
            assertThat(it.getTeam()).isEqualTo(5);
            assertThat(it.getSupplies()).isEqualTo(6);
            assertThat(it.getAether()).isEqualTo(7);
            assertThat(it.getFocus()).isEqualTo(8);
            assertThat(it.getCommander()).isEqualTo(bob);
            assertThat(it.getAbilities()).containsExactly(feedback, mindControl, maelstrom);
            assertThat(it.getActiveAbilities()).isEmpty();
            assertThat(it.getUsedAbilities()).containsExactly(mindControl);
        });
        assertThat(gameState.getTiles()).isUnmodifiable().hasSize(12).allSatisfy(it -> {
            assertThat(it.getGameState()).isSameAs(gameState);
            assertThat(gameState.getTile(it.getX(), it.getY())).isSameAs(it);
        });
        assertThat(gameState.getTile(0, 0)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(road);
            assertThat(it.getUnit()).isNotNull().satisfies(unit -> {
                assertThat(unit.getLocation()).isEqualTo(it);
                assertThat(unit.getType()).isEqualTo(dragoon);
                assertThat(unit.getOwner()).isEqualTo(gameState.getPlayer(0));
                assertThat(unit.getHpPercent()).isEqualTo(0.8);
                assertThat(unit.getActiveAbilities()).containsExactly(singularityCharge);
            });
        });
        assertThat(gameState.getTile(1, 0)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(road);
            assertThat(it.getUnit()).isNotNull().satisfies(unit -> {
                assertThat(unit.getLocation()).isEqualTo(it);
                assertThat(unit.getType()).isEqualTo(zealot);
                assertThat(unit.getOwner()).isEqualTo(gameState.getPlayer(0));
                assertThat(unit.getHpPercent()).isEqualTo(0.6);
                assertThat(unit.getActiveAbilities()).isEmpty();
            });
        });
        assertThat(gameState.getTile(2, 0)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(ocean);
            assertThat(it.getUnit()).isNull();
        });
        assertThat(gameState.getTile(3, 0)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(ocean);
            assertThat(it.getUnit()).isNull();
        });
        assertThat(gameState.getTile(0, 1)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(road);
            assertThat(it.getUnit()).isNull();
        });
        assertThat(gameState.getTile(1, 1)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(plains);
            assertThat(it.getUnit()).isNull();
        });
        assertThat(gameState.getTile(2, 1)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(plains);
            assertThat(it.getUnit()).isNull();
        });
        assertThat(gameState.getTile(3, 1)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(road);
            assertThat(it.getUnit()).isNull();
        });
        assertThat(gameState.getTile(0, 2)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(ocean);
            assertThat(it.getUnit()).isNull();
        });
        assertThat(gameState.getTile(1, 2)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(ocean);
            assertThat(it.getUnit()).isNull();
        });
        assertThat(gameState.getTile(2, 2)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(road);
            assertThat(it.getUnit()).isNotNull().satisfies(unit -> {
                assertThat(unit.getLocation()).isEqualTo(it);
                assertThat(unit.getType()).isEqualTo(zealot);
                assertThat(unit.getOwner()).isEqualTo(gameState.getPlayer(1));
                assertThat(unit.getHpPercent()).isEqualTo(0.6);
                assertThat(unit.getActiveAbilities()).isEmpty();
            });
        });
        assertThat(gameState.getTile(3, 2)).isNotNull().satisfies(it -> {
            assertThat(it.getType()).isEqualTo(road);
            assertThat(it.getUnit()).isNotNull().satisfies(unit -> {
                assertThat(unit.getLocation()).isEqualTo(it);
                assertThat(unit.getType()).isEqualTo(dragoon);
                assertThat(unit.getOwner()).isEqualTo(gameState.getPlayer(1));
                assertThat(unit.getHpPercent()).isEqualTo(0.8);
                assertThat(unit.getActiveAbilities()).isEmpty();
            });
        });
    }

}
