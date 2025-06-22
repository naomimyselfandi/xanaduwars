package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.TileData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.TileTagQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.TerrainType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.TileType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TileImplTest {

    @Mock
    private Player player;

    @Mock
    private Structure structure;

    @Mock
    private StructureType structureType;

    @Mock
    private Tile anotherTile;

    @Mock
    private TileType type, anotherType;

    @Mock
    private Unit unit;

    @Mock
    private Ruleset ruleset;

    private TileData tileData;

    @Mock
    private GameState gameState;

    private TileId id;

    private TileImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        tileData = random.get();
        id = random.get();
        fixture = new TileImpl(tileData, gameState, id);
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getTileType(tileData.getTypeId())).thenReturn(type);
    }

    @Test
    void getType() {
        assertThat(fixture.getType()).isEqualTo(type);
    }

    @Test
    void setType(SeededRng random) {
        var anotherTypeId = random.<TileTypeId>get();
        when(anotherType.getId()).thenReturn(anotherTypeId);
        assertThat(fixture.setType(anotherType)).isSameAs(fixture);
        assertThat(tileData.getTypeId()).isEqualTo(anotherTypeId);
        verify(gameState).invalidateCache();
    }

    @Test
    void getTags(SeededRng random) {
        var value = Set.<TileTag>of(random.get(), random.get());
        when(gameState.evaluate(new TileTagQuery(fixture))).thenReturn(value);
        assertThat(fixture.getTags()).isEqualTo(value);
    }

    @Test
    void getStructure() {
        assertThat(fixture.getStructure()).isNull();
        when(gameState.getStructures()).thenReturn(new TreeMap<>(Map.of(id.structureId(), structure)));
        assertThat(fixture.getStructure()).isEqualTo(structure);
    }

    @Test
    void getUnit() {
        assertThat(fixture.getUnit()).isNull();
        when(gameState.getUnit(fixture)).thenReturn(unit);
        assertThat(fixture.getUnit()).isEqualTo(unit);
    }

    private enum MovementCostTestCase {ONE_MATCH, MULTIPLE_MATCHES, NO_MATCH}

    @EnumSource
    @ParameterizedTest
    void getMovementCost(MovementCostTestCase testCase, SeededRng random) {
        getMovementCost(testCase, type, random);
    }

    @EnumSource
    @ParameterizedTest
    void getMovementCost_WhenTheTileHasAStructure_ThenDelegates(MovementCostTestCase testCase, SeededRng random) {
        when(gameState.getStructures()).thenReturn(new TreeMap<>(Map.of(id.structureId(), structure)));
        when(structure.getType()).thenReturn(structureType);
        getMovementCost(testCase, structureType, random);
    }

    private void getMovementCost(MovementCostTestCase testCase, TerrainType type, SeededRng random) {
        var foo = random.<UnitTag>get();
        var fooValue = random.nextDouble();
        var bar = random.<UnitTag>get();
        var barValue = random.nextDouble();
        when(type.getMovementTable()).thenReturn(Map.of(foo, fooValue, bar, barValue));
        when(unit.getTags()).thenReturn(switch (testCase) {
            case ONE_MATCH -> Set.of(foo, random.not(foo, bar), random.not(foo, bar));
            case MULTIPLE_MATCHES -> Set.of(foo, bar, random.not(foo, bar));
            case NO_MATCH -> Set.of(random.not(foo, bar), random.not(foo, bar), random.not(foo, bar));
        });
        assertThat(fixture.getMovementCost(unit)).isEqualByComparingTo(switch (testCase) {
            case ONE_MATCH -> fooValue;
            case MULTIPLE_MATCHES -> Math.min(fooValue, barValue);
            case NO_MATCH -> Double.NaN;
        });
    }

    @Test
    void getCover(SeededRng random) {
        var value = random.nextDouble();
        when(type.getCover()).thenReturn(value);
        assertThat(fixture.getCover()).isEqualTo(value);
    }

    @Test
    void getCover_WhenTheTileHasAStructure_ThenDelegates(SeededRng random) {
        var value = random.nextDouble();
        when(gameState.getStructures()).thenReturn(new TreeMap<>(Map.of(id.structureId(), structure)));
        when(structure.getType()).thenReturn(structureType);
        when(structureType.getCover()).thenReturn(value);
        assertThat(fixture.getCover()).isEqualTo(value);
    }

    @RepeatedTest(4)
    void getDistance(SeededRng random) {
        var dx = random.nextInt(-50, 50);
        var dy = random.nextInt(-50, 50);
        var expected = Math.abs(dx) + Math.abs(dy);
        when(anotherTile.getId()).thenReturn(new TileId(id.x() + dx, id.y() + dy));
        assertThat(fixture.getDistance(anotherTile)).isEqualTo(expected);
    }

    @EnumSource
    @ParameterizedTest
    void step(Direction direction) {
        var anotherId = switch (direction) {
            case NORTH -> new TileId(id.x(), id.y() - 1);
            case EAST -> new TileId(id.x() + 1, id.y());
            case SOUTH -> new TileId(id.x(), id.y() + 1);
            case WEST -> new TileId(id.x() - 1, id.y());
        };
        when(gameState.getTiles()).thenReturn(new TreeMap<>(Map.of(id, fixture, anotherId, anotherTile)));
        assertThat(fixture.step(direction)).isEqualTo(anotherTile);
    }

    @Test
    void getArea() {
        var dx = -2;
        var dy = -2;
        var map = new TreeMap<TileId, Tile>();
        var tiles = IntStream.rangeClosed(0, 25).mapToObj(i -> {
            var tile = mock(Tile.class);
            when(tile.toString()).thenReturn("mockTile(%d, %d)".formatted(i % 5, i / 5)); // For debugging
            return tile;
        }).toList();
        for (var tile : tiles) {
            map.put(new TileId(id.x() + dx, id.y() + dy), tile);
            if (dx == 2) {
                dx = -2;
                dy++;
            } else {
                dx++;
            }
        }
        when(gameState.getTiles()).thenReturn(map);
        assertThat(fixture.getArea(0)).containsExactly(map.get(id));
        assertThat(fixture.getArea(1)).containsExactly(
                map.get(new TileId(id.x(), id.y() - 1)),
                map.get(new TileId(id.x() - 1, id.y())),
                map.get(id),
                map.get(new TileId(id.x() + 1, id.y())),
                map.get(new TileId(id.x(), id.y() + 1))
        );
        assertThat(fixture.getArea(2)).containsExactly(
                map.get(new TileId(id.x(), id.y() - 2)),
                map.get(new TileId(id.x() - 1, id.y() - 1)),
                map.get(new TileId(id.x(), id.y() - 1)),
                map.get(new TileId(id.x() + 1, id.y() - 1)),
                map.get(new TileId(id.x() - 2, id.y())),
                map.get(new TileId(id.x() - 1, id.y())),
                map.get(id),
                map.get(new TileId(id.x() + 1, id.y())),
                map.get(new TileId(id.x() + 2, id.y())),
                map.get(new TileId(id.x() - 1, id.y() + 1)),
                map.get(new TileId(id.x(), id.y() + 1)),
                map.get(new TileId(id.x() + 1, id.y() + 1)),
                map.get(new TileId(id.x(), id.y() + 2))
        );
    }

    @Test
    void getMemory(SeededRng random) {
        var playerId = random.<PlayerId>get();
        when(player.getId()).thenReturn(playerId);
        var structureTypeId = random.<StructureTypeId>get();
        when(ruleset.getStructureType(structureTypeId)).thenReturn(structureType);
        tileData.setMemory(playerId, structureTypeId);
        assertThat(fixture.getMemory(player)).isEqualTo(structureType);
    }

    @Test
    void setMemory(SeededRng random) {
        var playerId = random.<PlayerId>get();
        when(player.getId()).thenReturn(playerId);
        var structureTypeId = random.<StructureTypeId>get();
        when(structureType.getId()).thenReturn(structureTypeId);
        assertThat(fixture.setMemory(player, structureType)).isSameAs(fixture);
        assertThat(tileData.getMemory(playerId)).isEqualTo(structureTypeId);
        verify(gameState).invalidateCache();
        fixture.setMemory(player, null);
        assertThat(fixture.getMemory(player)).isNull();
        verify(gameState, times(2)).invalidateCache();
    }

    @Test
    void getRules() {
        assertThat(fixture.getRules()).containsExactly(type);
    }

    @Test
    void getRules_WhenTheTileHasAStructure_ThenDelegates() {
        when(structure.getType()).thenReturn(structureType);
        when(gameState.getStructures()).thenReturn(new TreeMap<>(Map.of(id.structureId(), structure)));
        assertThat(fixture.getRules()).containsExactly(structureType);
    }

    @Test
    void getOwner() {
        assertThat(fixture.getOwner()).isNull();
    }

    @Test
    void getActions() {
        assertThat(fixture.getActions()).isEmpty();
    }

    @Test
    void getTile() {
        assertThat(fixture.getTile()).isSameAs(fixture);
    }

}
