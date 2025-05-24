package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.*;
import io.github.naomimyselfandi.xanaduwars.core.Resource;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.EnumMap;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class GameDataFactoryImplTest {

    private final UUID sourceId = UUID.randomUUID();

    private final GameDataFactoryImpl fixture = new GameDataFactoryImpl();

    @RepeatedTest(10)
    void create_GameData(SeededRandom random) {
        var source = createGameData(random);
        var copy = fixture.create(source);
        assertIsSuccessfulClone(copy, source);
    }

    @RepeatedTest(10)
    void create_GameMap(SeededRandom random) {
        var versionNumber = versionNumber(random);
        var source = createGameMap(random);
        var copy = fixture.create(source, versionNumber);
        assertIsSuccessfulClone(copy, source);
        assertThat(copy.versionNumber()).isEqualTo(versionNumber);
    }

    private GameData createGameData(Random random) {
        var result = new GameData()
                .turn(random.nextInt())
                .activePlayer(new PlayerId(random.nextInt(Integer.MAX_VALUE)));
        result.versionNumber(versionNumber(random));
        return initialize(result, random);
    }

    private GameMap createGameMap(Random random) {
        var result = new GameMap().name("TestMap" + random.nextInt());
        return initialize(result, random);
    }

    private <T extends LowLevelData> T initialize(T result, Random random) {
        var width = random.nextInt(8, 12);
        for (var field : LowLevelData.Fields.values()) {
            var _ = switch (field) {
                case id -> result.id(sourceId);
                case width -> result.width(width);
                case players -> {
                    createPlayers(result, random);
                    yield null;
                }
                case tiles -> {
                    createTiles(result, random, width);
                    yield null;
                }
                case units -> {
                    createUnits(result, random);
                    yield null;
                }
                case nextUnitId -> null;
            };
        }
        return result;
    }

    private void createPlayers(LowLevelData source, Random random) {
        IntStream
                .range(0, random.nextInt(2, 8))
                .mapToObj(position -> createPlayer(position, random))
                .forEach(source.players()::add);
    }

    private PlayerData createPlayer(int playerId, Random random) {
        var result = new PlayerData();
        for (var field : PlayerData.Fields.values()) {
            var _ = switch (field) {
                case playerId -> result.playerId(new PlayerId(playerId));
                case team -> result.team(random.nextInt());
                case defeated -> result.defeated(random.nextBoolean());
                case playerType -> result.playerType(random.nextInt());
                case resources -> {
                    var resources = new EnumMap<Resource, Integer>(Resource.class);
                    for (var resource : Resource.values()) {
                        resources.put(resource, random.nextInt());
                    }
                    yield result.resources(resources);
                }
                case knownSpells -> result.knownSpells(IntStream
                        .range(0, random.nextInt(3, 6))
                        .mapToObj(_ -> random.nextInt())
                        .toList());
                case activeSpells -> result.activeSpells(IntStream
                        .range(0, random.nextInt(3, 6))
                        .mapToObj(_ -> random.nextInt())
                        .toList());
            };
        }
        return result;
    }

    private void createTiles(LowLevelData source, Random random, int width) {
        var height = random.nextInt(8, 12);
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                source.tiles().add(createTile(x, y, random));
            }
        }
    }

    private TileData createTile(int x, int y, Random random) {
        var result = new TileData();
        for (var field : TileData.Fields.values()) {
            var _ = switch (field) {
                case tileId -> result.tileId(new TileId(x, y));
                case tileType -> result.tileType(random.nextInt());
                case structureType -> result.structureType(random.nextInt());
                case owner -> result.owner(new PlayerId(random.nextInt()));
                case hitpoints -> result.hitpoints(new Percent(random.nextDouble()));
                case construction -> result.construction(createConstruction(random));
                case memory -> result.memory(IntStream
                        .range(0, random.nextInt(0, 3))
                        .boxed()
                        .collect(Collectors.toMap(
                                _ -> new PlayerId(random.nextInt(Integer.MAX_VALUE)),
                                _ -> random.nextInt()
                        )));
            };
        }
        return result;
    }

    private @Nullable ConstructionData createConstruction(Random random) {
        if (random.nextBoolean()) return null;
        var result = new ConstructionData();
        for (var field : ConstructionData.Fields.values()) {
            var _ = switch (field) {
                case structureType -> result.structureType(random.nextInt());
                case progress -> result.progress(new Percent(random.nextDouble()));
            };
        }
        return result;
    }

    private void createUnits(LowLevelData source, Random random) {
        IntStream
                .range(0, random.nextInt(2, 8))
                .mapToObj(unitId -> createUnit(unitId, random))
                .forEach(unit -> source.units().put(unit.unitId(), unit));
        source.nextUnitId(source.units().size());
    }

    private UnitData createUnit(int unitId, Random random) {
        var result = new UnitData();
        for (var field : UnitData.Fields.values()) {
            var _ = switch (field) {
                case unitId -> result.unitId(new UnitId(unitId));
                case unitType -> result.unitType(random.nextInt());
                case owner -> result.owner(new PlayerId(random.nextInt()));
                case location -> result.location(NodeId.withIntValue(random.nextInt()));
                case canAct -> result.canAct(random.nextBoolean());
                case hitpoints -> result.hitpoints(new Percent(random.nextDouble()));
            };
        }
        return result;
    }

    private void assertIsSuccessfulClone(GameData actual, LowLevelData expected) {
        assertThat(actual.id()).isNotEqualTo(expected.id());
        assertThat(actual.players()).hasSameSizeAs(expected.players());
        for (var i = 0; i < expected.players().size(); i++) {
            assertHaveSameFields(actual.players().get(i), expected.players().get(i));
        }
        assertThat(actual.tiles()).hasSameSizeAs(expected.tiles());
        for (var i = 0; i < expected.tiles().size(); i++) {
            assertHaveSameFields(actual.tiles().get(i), expected.tiles().get(i));
        }
        assertThat(actual.units().keySet()).isEqualTo(expected.units().keySet());
        for (var i : expected.units().keySet()) {
            assertHaveSameFields(actual.units().get(i), expected.units().get(i));
        }
        if (expected instanceof GameData expectedGameData) {
            assertHaveSameGlobalFields(actual, expectedGameData);
        } else {
            assertHasDefaultGlobalFields(actual);
        }
    }

    private void assertHaveSameFields(PlayerData actual, PlayerData expected) {
        for (var field : PlayerData.Fields.values()) {
            Function<PlayerData, Object> extractor = switch (field) {
                case playerId -> PlayerData::playerId;
                case team -> PlayerData::team;
                case defeated -> PlayerData::defeated;
                case playerType -> PlayerData::playerType;
                case resources -> PlayerData::resources;
                case knownSpells -> PlayerData::knownSpells;
                case activeSpells -> PlayerData::activeSpells;
            };
            assertThat(extractor.apply(actual)).isEqualTo(extractor.apply(expected));
        }
    }

    private void assertHaveSameFields(TileData actual, TileData expected) {
        for (var field : TileData.Fields.values()) {
            Function<TileData, Object> extractor = switch (field) {
                case tileId -> TileData::tileId;
                case tileType -> TileData::tileType;
                case structureType -> TileData::structureType;
                case owner -> TileData::owner;
                case hitpoints -> TileData::hitpoints;
                case construction -> TileData::construction;
                case memory -> TileData::memory;
            };
            assertThat(extractor.apply(actual)).isEqualTo(extractor.apply(expected));
        }
        if (actual.construction() != null) {
            assertThat(actual.construction()).isNotSameAs(expected.construction());
        }
    }

    private void assertHaveSameFields(UnitData actual, UnitData expected) {
        for (var field : UnitData.Fields.values()) {
            Function<UnitData, Object> extractor = switch (field) {
                case unitId -> UnitData::unitId;
                case unitType -> UnitData::unitType;
                case owner -> UnitData::owner;
                case location -> UnitData::location;
                case canAct -> UnitData::canAct;
                case hitpoints -> UnitData::hitpoints;
            };
            assertThat(extractor.apply(actual)).isEqualTo(extractor.apply(expected));
        }
    }

    private void assertHaveSameGlobalFields(GameData actual, GameData expected) {
        for (var field : GameData.Fields.values()) {
            Function<GameData, Object> extractor = switch (field) {
                case versionNumber -> GameData::versionNumber;
                case turn -> GameData::turn;
                case activePlayer -> GameData::activePlayer;
            };
            assertThat(extractor.apply(actual)).isEqualTo(extractor.apply(expected));
        }
    }

    private void assertHasDefaultGlobalFields(GameData actual) {
        for (var field : GameData.Fields.values()) {
            var _ = switch (field) {
                case versionNumber -> null; // Handled externally
                case turn -> assertThat(actual.turn()).isZero();
                case activePlayer -> assertThat(actual.activePlayer()).isEqualTo(new PlayerId(0));
            };
        }
    }

    private static VersionNumber versionNumber(Random random) {
        int major = random.nextInt(255);
        int minor = random.nextInt(255);
        int patch = random.nextInt(255);
        return new VersionNumber("%s.%s.%s".formatted(major, minor, patch));
    }

}
