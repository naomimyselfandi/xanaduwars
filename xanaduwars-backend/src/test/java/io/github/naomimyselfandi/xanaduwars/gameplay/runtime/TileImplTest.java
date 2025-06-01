package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.ConstructionData;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.TileData;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TileDestroyedEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TileImplTest {

    @Mock
    private AugmentedGameState gameState;

    @Mock
    private Ruleset ruleset;

    private int x, y;

    @Mock
    private TileType tileType;

    private TileData tileData;

    private TileImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        lenient().when(gameState.ruleset()).thenReturn(ruleset);
        var tileTypeId = new TileTypeId(random.nextInt(8));
        var tileTypes = IntStream.range(0, 8).mapToObj(i -> i == tileTypeId.index() ? tileType : mock()).toList();
        lenient().when(ruleset.tileTypes()).thenReturn(tileTypes);
        var tileId = random.nextTileId();
        x = tileId.x();
        y = tileId.y();
        tileData = new TileData().tileId(tileId).tileType(tileTypeId);
        fixture = new TileImpl(gameState, tileData);
    }

    @RepeatedTest(3)
    void type(SeededRng random) {
        var tileTypes = IntStream.range(0, 8).mapToObj(_ -> mock(TileType.class)).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var tileTypeId = new TileTypeId(random.nextInt(tileTypes.size()));
        var tileType = tileTypes.get(tileTypeId.index());
        tileData.tileType(tileTypeId);
        assertThat(fixture.type()).isEqualTo(tileType);
    }

    @RepeatedTest(3)
    void foundation(SeededRng random) {
        assertThat(fixture.foundation()).isEmpty();
        var tileTypes = IntStream.range(0, 8).mapToObj(_ -> mock(TileType.class)).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var structureTypeId = new TileTypeId(random.nextInt(tileTypes.size()));
        var structureType = tileTypes.get(structureTypeId.index());
        tileData.structureType(structureTypeId);
        assertThat(fixture.foundation()).contains(structureType);
        assertThat(fixture.type()).isEqualTo(structureType);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,0
            0,1,1
            1,0,1
            1,1,2
            1,2,3
            """)
    void distance(int x, int y, int distance) {
        var other = mock(Tile.class);
        when(other.id()).thenReturn(new TileId(this.x + x, this.y + y));
        assertThat(fixture.distance(other)).isEqualTo(distance);
    }

    @Test
    void unit() {
        var foo = mock(Unit.class);
        when(foo.location()).thenReturn(fixture);
        var bar = mock(Unit.class);
        when(bar.location()).thenReturn(foo);
        when(gameState.units()).thenReturn(List.of(bar, foo));
        assertThat(fixture.unit()).contains(foo);
    }

    @Test
    void area() {
        var tiles = List.<List<Tile>>of(
                List.of(mock(), mock(), mock(), mock(), mock()),
                List.of(mock(), mock(), mock(), mock(), mock()),
                List.of(mock(), mock(), fixture, mock(), mock()),
                List.of(mock(), mock(), mock(), mock(), mock()),
                List.of(mock(), mock(), mock(), mock(), mock())
        );
        when(gameState.tile(anyInt(), anyInt())).then(invocation -> {
            var x2 = invocation.<Integer>getArgument(0);
            var y2 = invocation.<Integer>getArgument(1);
            return Optional.of(tiles.get(y2 - y + 2).get(x2 - x + 2));
        });
        assertThat(fixture.area(2).toList()).containsExactly(
                tiles.get(0).get(2),
                tiles.get(1).get(1),
                tiles.get(1).get(2),
                tiles.get(1).get(3),
                tiles.get(2).get(0),
                tiles.get(2).get(1),
                tiles.get(2).get(2),
                tiles.get(2).get(3),
                tiles.get(2).get(4),
                tiles.get(3).get(1),
                tiles.get(3).get(2),
                tiles.get(3).get(3),
                tiles.get(4).get(2)
        );
    }

    @EnumSource
    @ParameterizedTest
    void step(Direction direction) {
        var tile = mock(Tile.class);
        var x2 = x + switch (direction) {
            case NORTH, SOUTH -> 0;
            case EAST -> 1;
            case WEST -> -1;
        };
        var y2 = y + switch (direction) {
            case NORTH -> -1;
            case EAST, WEST -> 0;
            case SOUTH -> 1;
        };
        when(gameState.tile(x2, y2)).thenReturn(Optional.of(tile));
        assertThat(fixture.step(direction)).contains(tile);
    }

    @RepeatedTest(3)
    void movementTable(SeededRng random) {
        var foo = random.nextTag();
        var bar = random.nextTag();
        var table = Map.of(foo, random.nextDouble(), bar, random.nextDouble());
        when(tileType.movementTable()).thenReturn(table);
        assertThat(fixture.movementTable()).isEqualTo(table);
    }

    @RepeatedTest(3)
    void cover(SeededRng random) {
        var cover = random.nextPercent();
        when(tileType.cover()).thenReturn(cover);
        assertThat(fixture.cover()).isEqualTo(cover);
    }

    @RepeatedTest(3)
    void income(SeededRng random) {
        var income = Map.of(random.pick(Resource.values()), random.nextInt(5000));
        when(tileType.income()).thenReturn(income);
        assertThat(fixture.income()).isEqualTo(income);
    }

    @RepeatedTest(3)
    void deploymentRoster() {
        var deploymentRoster = Set.of(mock(UnitType.class));
        when(tileType.deploymentRoster()).thenReturn(deploymentRoster);
        assertThat(fixture.deploymentRoster()).isEqualTo(deploymentRoster);
    }

    @RepeatedTest(3)
    void construction(SeededRng random) {
        var tileTypes = IntStream.range(0, 8).mapToObj(_ -> mock(TileType.class)).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var structureTypeId = new TileTypeId(random.nextInt(tileTypes.size()));
        var structureType = tileTypes.get(structureTypeId.index());
        when(structureType.id()).thenReturn(structureTypeId);
        var progress = new Percent(random.nextDouble());
        assertThat(fixture.construction()).isEmpty();
        fixture.construction(new Construction(structureType, progress));
        assertThat(fixture.construction()).contains(new Construction(structureType, progress));
        assertThat(tileData.construction())
                .isNotNull()
                .returns(structureTypeId, ConstructionData::structureType)
                .returns(progress, ConstructionData::progress);
    }

    @RepeatedTest(3)
    void createStructure(SeededRng random) {
        tileData.hitpoints(new Percent(random.nextDouble()))
                .construction(new ConstructionData(random.nextTileTypeId(), random.nextPercent()));
        var tileTypes = IntStream.range(0, 8).mapToObj(_ -> mock(TileType.class)).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var structureTypeId = new TileTypeId(random.nextInt(tileTypes.size()));
        var structureType = tileTypes.get(structureTypeId.index());
        when(structureType.id()).thenReturn(structureTypeId);
        var players = IntStream.range(0, 8).mapToObj(_ -> mock(Player.class)).toList();
        when(gameState.players()).thenReturn(players);
        var playerId = new PlayerId(random.nextInt(players.size()));
        var player = players.get(playerId.intValue());
        when(player.id()).thenReturn(playerId);
        fixture.createStructure(structureType, player);
        assertThat(fixture.construction()).isEmpty();
        assertThat(fixture.hp()).isEqualTo(Percent.FULL);
        assertThat(fixture.type()).isEqualTo(structureType);
        assertThat(fixture.owner()).contains(player);
        assertThat(tileData.construction()).isNull();
        assertThat(tileData.hitpoints()).isEqualTo(Percent.FULL);
        assertThat(tileData.structureType()).isEqualTo(structureTypeId);
        assertThat(tileData.owner()).isEqualTo(playerId);
    }

    @Test
    void createUnit() {
        var unit = mock(Unit.class);
        var unitType = mock(UnitType.class);
        var player = mock(Player.class);
        when(gameState.createUnit(fixture, unitType)).thenReturn(unit);
        fixture.createUnit(unitType, player);
        verify(gameState).createUnit(fixture, unitType);
        verify(unit).owner(player);
    }

    @Test
    void tile() {
        assertThat(fixture.tile()).contains(fixture);
    }

    @RepeatedTest(3)
    void onDestruction(SeededRng random) {
        var hitpoints = random.nextPercent();
        var structureType = random.nextTileTypeId();
        var owner = random.nextPlayerId();
        var constructionType = random.nextTileTypeId();
        var progress = random.nextPercent();
        var constructionData = new ConstructionData(constructionType, progress);
        tileData.hitpoints(hitpoints).structureType(structureType).owner(owner).construction(constructionData);
        when(gameState.evaluate(new TileDestroyedEvent(fixture))).then(_ -> {
            // Since we can't use inOrder with something that isn't a mock...
            assertThat(fixture.hp()).isEqualTo(hitpoints);
            assertThat(tileData.hitpoints()).isEqualTo(hitpoints);
            assertThat(tileData.structureType()).isEqualTo(structureType);
            assertThat(tileData.owner()).isEqualTo(owner);
            assertThat(tileData.construction()).isEqualTo(constructionData);
            return None.NONE;
        });
        fixture.onDestruction();
        assertThat(tileData.hitpoints()).isEqualTo(Percent.FULL);
        assertThat(tileData.structureType()).isNull();
        assertThat(tileData.owner()).isNull();
        assertThat(tileData.construction()).isNull();
        verify(gameState).evaluate(new TileDestroyedEvent(fixture));
    }

}
