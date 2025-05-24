package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.data.ConstructionData;
import io.github.naomimyselfandi.xanaduwars.core.data.TileData;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.*;
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
    void setup(SeededRandom random) {
        lenient().when(gameState.ruleset()).thenReturn(ruleset);
        var tileTypeId = random.nextInt(8);
        var tileTypes = IntStream.range(0, 8).mapToObj(i -> i == tileTypeId ? tileType : mock()).toList();
        lenient().when(ruleset.tileTypes()).thenReturn(tileTypes);
        x = random.nextInt(255);
        y = random.nextInt(255);
        tileData = new TileData().tileId(new TileId(x, y)).tileType(tileTypeId);
        fixture = new TileImpl(gameState, tileData);
    }

    @RepeatedTest(3)
    void type(SeededRandom random) {
        var tileTypes = IntStream.range(0, 8).mapToObj(_ -> mock(TileType.class)).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var tileTypeId = random.nextInt(tileTypes.size());
        var tileType = tileTypes.get(tileTypeId);
        tileData.tileType(tileTypeId);
        assertThat(fixture.type()).isEqualTo(tileType);
    }

    @RepeatedTest(3)
    void foundation(SeededRandom random) {
        assertThat(fixture.foundation()).isEmpty();
        var tileTypes = IntStream.range(0, 8).mapToObj(_ -> mock(TileType.class)).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var structureTypeId = random.nextInt(tileTypes.size());
        var structureType = tileTypes.get(structureTypeId);
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
    void movementTable(SeededRandom random) {
        var foo = new Tag("F" + random.nextInt(Integer.MAX_VALUE));
        var bar = new Tag("B" + random.nextInt(Integer.MAX_VALUE));
        var table = Map.of(foo, random.nextDouble(), bar, random.nextDouble());
        when(tileType.movementTable()).thenReturn(table);
        assertThat(fixture.movementTable()).isEqualTo(table);
    }

    @RepeatedTest(3)
    void cover(SeededRandom random) {
        var cover = Percent.withDoubleValue(random.nextDouble());
        when(tileType.cover()).thenReturn(cover);
        assertThat(fixture.cover()).isEqualTo(cover);
    }

    @RepeatedTest(3)
    void income(SeededRandom random) {
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
    void construction(SeededRandom random) {
        var tileTypes = IntStream.range(0, 8).mapToObj(_ -> mock(TileType.class)).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var structureTypeId = random.nextInt(tileTypes.size());
        var structureType = tileTypes.get(structureTypeId);
        when(structureType.index()).thenReturn(structureTypeId);
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
    void createStructure(SeededRandom random) {
        tileData.hitpoints(new Percent(random.nextDouble()))
                .construction(new ConstructionData()
                        .structureType(random.nextInt(Integer.MAX_VALUE))
                        .progress(new Percent(random.nextDouble())));
        var tileTypes = IntStream.range(0, 8).mapToObj(_ -> mock(TileType.class)).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var structureTypeId = random.nextInt(tileTypes.size());
        var structureType = tileTypes.get(structureTypeId);
        when(structureType.index()).thenReturn(structureTypeId);
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
    void onDestruction(SeededRandom random) {
        tileData.hitpoints(new Percent(random.nextDouble()))
                .structureType(random.nextInt(Integer.MAX_VALUE))
                .owner(new PlayerId(random.nextInt(Integer.MAX_VALUE)))
                .construction(new ConstructionData()
                        .structureType(random.nextInt(Integer.MAX_VALUE))
                        .progress(new Percent(random.nextDouble())));
        fixture.onDestruction();
        assertThat(tileData.hitpoints()).isEqualTo(Percent.FULL);
        assertThat(tileData.structureType()).isNull();
        assertThat(tileData.owner()).isNull();
        assertThat(tileData.construction()).isNull();
    }

}
