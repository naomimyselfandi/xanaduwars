package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.TileData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TileImplTest {

    @Mock
    private UnitType unitType;

    @Mock
    private StructureType structureType;

    @Mock
    private Player player;

    @Mock
    private Tile anotherTile, yetAnotherTile;

    @Mock
    private Unit unit, anotherUnit;

    @Mock
    private TileType tileType;

    @Mock
    private Ruleset ruleset;

    private TileData tileData;

    @Mock
    private GameState gameState;

    @Mock
    private Creator creator;

    private TileImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        tileData = random.nextTileData().structureData(null);
        when(ruleset.tileType(tileData.type())).thenReturn(tileType);
        fixture = new TileImpl(tileData, gameState, ruleset, creator);
    }

    @Test
    void id() {
        assertThat(fixture.id()).isEqualTo(tileData.id());
    }

    @Test
    void type() {
        assertThat(fixture.type()).isEqualTo(tileType);
    }

    @Test
    void tags(SeededRng random) {
        when(tileType.tags()).thenReturn(Set.of(random.get(), random.get()));
        assertThat(fixture.tags()).isEqualTo(tileType.tags());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void movementTable(boolean hasStructure, SeededRng random) {
        when(tileType.movementTable()).thenReturn(random.get());
        when(structureType.movementTable()).thenReturn(random.get());
        if (hasStructure) {
            var structureData = random.nextStructureData();
            tileData.structureData(structureData);
            when(ruleset.structureType(structureData.type())).thenReturn(structureType);
        }
        var type = hasStructure ? structureType : tileType;
        assertThat(fixture.movementTable()).isEqualTo(type.movementTable());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void cost(boolean hasStructure, SeededRng random) {
        when(tileType.movementTable()).thenReturn(random.get());
        when(structureType.movementTable()).thenReturn(random.get());
        if (hasStructure) {
            var structureData = random.nextStructureData();
            tileData.structureData(structureData);
            when(ruleset.structureType(structureData.type())).thenReturn(structureType);
        }
        var type = hasStructure ? structureType : tileType;
        var tags = Set.of(random.pick(type.movementTable().table().keySet()), random.get());
        when(unit.tags()).thenReturn(tags);
        assertThat(fixture.cost(unit)).isEqualTo(type.movementTable().cost(unit.tags()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void cover(boolean hasStructure, SeededRng random) {
        when(tileType.cover()).thenReturn(random.nextDouble());
        when(structureType.cover()).thenReturn(random.nextDouble());
        if (hasStructure) {
            var structureData = random.nextStructureData();
            tileData.structureData(structureData);
            when(ruleset.structureType(structureData.type())).thenReturn(structureType);
        }
        var type = hasStructure ? structureType : tileType;
        assertThat(fixture.cover()).isEqualTo(type.cover());
    }

    @Test
    void structure(SeededRng random) {
        var structureData = random.nextStructureData();
        tileData.structureData(structureData);
        assertThat(fixture.structure()).isEqualTo(new StructureImpl(structureData, fixture, ruleset));
        tileData.structureData(null);
        assertThat(fixture.structure()).isNull();
    }

    @Test
    void unit() {
        assertThat(fixture.unit()).isNull();
        when(gameState.units()).then(_ -> Stream.of(unit, anotherUnit));
        when(unit.location()).thenReturn(fixture);
        when(anotherUnit.location()).thenReturn(anotherTile);
        assertThat(fixture.unit()).isEqualTo(unit);
    }

    @Test
    void createUnit(SeededRng random) {
        var unitTypeId = random.<UnitTypeId>get();
        when(unitType.id()).thenReturn(unitTypeId);
        var playerId = random.<PlayerId>get();
        when(player.id()).thenReturn(playerId);
        fixture.createUnit(unitType, player);
        verify(creator).createUnitData(fixture.id(), unitTypeId, playerId);
    }

    @Test
    void createStructure(SeededRng random) {
        var structureTypeId = random.<StructureTypeId>get();
        when(structureType.id()).thenReturn(structureTypeId);
        var playerId = random.<PlayerId>get();
        when(player.id()).thenReturn(playerId);
        fixture.createStructure(structureType, player);
        verify(creator).createStructureData(fixture.id(), structureTypeId, playerId);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,1,2,3,4
            1,0,0,1,2
            """)
    void distance(int x0, int y0, int x1, int y1, int expected) {
        tileData.id(new TileId(x0, y0));
        when(anotherTile.id()).thenReturn(new TileId(x1, y1));
        assertThat(fixture.distance(anotherTile)).isEqualTo(expected);
        assertThat(fixture.distance(anotherTile)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,1,2,3,4
            1,0,0,1,2
            """)
    void distance_WhenTheOtherElementIsNotATile_ThenUsesItsTile(int x0, int y0, int x1, int y1, int expected) {
        when(unit.tile()).thenReturn(anotherTile);
        tileData.id(new TileId(x0, y0));
        when(anotherTile.id()).thenReturn(new TileId(x1, y1));
        assertThat(fixture.distance(unit)).isEqualTo(expected);
        assertThat(fixture.distance(unit)).isEqualTo(expected);
    }

    @Test
    void distance_WhenTheOtherElementIsAUnitInATransport_ThenNaN() {
        assertThat(fixture.distance(unit)).isNaN();
    }

    @EnumSource
    @ParameterizedTest
    void step(Direction direction) {
        when(gameState.maybeTile(tileData.id().step(direction))).thenReturn(anotherTile);
        assertThat(fixture.step(direction)).isEqualTo(anotherTile);
    }

    @Test
    void area() {
        var ids = tileData.id().area(1).iterator();
        when(gameState.maybeTile(ids.next())).thenReturn(null);
        when(gameState.maybeTile(ids.next())).thenReturn(anotherTile);
        when(gameState.maybeTile(ids.next())).thenReturn(fixture);
        when(gameState.maybeTile(ids.next())).thenReturn(yetAnotherTile);
        when(gameState.maybeTile(ids.next())).thenReturn(null);
        assertThat(fixture.area(1)).containsExactly(anotherTile, fixture, yetAnotherTile);
    }

    @Test
    void tile() {
        assertThat(fixture.tile()).isSameAs(fixture);
    }

    @Test
    void testToString() {
        var id = tileData.id();
        assertThat(fixture).hasToString("Tile[x=%d, y=%d, type=%s]", id.x(), id.y(), tileType);
    }

}
