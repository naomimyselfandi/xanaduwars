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
        tileData = random.nextTileData().setStructureData(null);
        when(ruleset.getTileTypes(tileData.getType())).thenReturn(tileType);
        fixture = new TileImpl(tileData, gameState, ruleset, creator);
    }

    @Test
    void getId() {
        assertThat(fixture.getId()).isEqualTo(tileData.getId());
    }

    @Test
    void getType() {
        assertThat(fixture.getType()).isEqualTo(tileType);
    }

    @Test
    void getTags(SeededRng random) {
        when(tileType.getTags()).thenReturn(Set.of(random.get(), random.get()));
        assertThat(fixture.getTags()).isEqualTo(tileType.getTags());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getMovementTable(boolean hasStructure, SeededRng random) {
        when(tileType.getMovementTable()).thenReturn(random.get());
        when(structureType.getMovementTable()).thenReturn(random.get());
        if (hasStructure) {
            var structureData = random.nextStructureData();
            tileData.setStructureData(structureData);
            when(ruleset.getStructureType(structureData.getType())).thenReturn(structureType);
        }
        var type = hasStructure ? structureType : tileType;
        assertThat(fixture.getMovementTable()).isEqualTo(type.getMovementTable());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getMovementCost(boolean hasStructure, SeededRng random) {
        when(tileType.getMovementTable()).thenReturn(random.get());
        when(structureType.getMovementTable()).thenReturn(random.get());
        if (hasStructure) {
            var structureData = random.nextStructureData();
            tileData.setStructureData(structureData);
            when(ruleset.getStructureType(structureData.getType())).thenReturn(structureType);
        }
        var type = hasStructure ? structureType : tileType;
        var tags = Set.of(random.pick(type.getMovementTable().table().keySet()), random.get());
        when(unit.getTags()).thenReturn(tags);
        assertThat(fixture.getMovementCost(unit)).isEqualTo(type.getMovementTable().cost(unit.getTags()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getCover(boolean hasStructure, SeededRng random) {
        when(tileType.getCover()).thenReturn(random.nextDouble());
        when(structureType.getCover()).thenReturn(random.nextDouble());
        if (hasStructure) {
            var structureData = random.nextStructureData();
            tileData.setStructureData(structureData);
            when(ruleset.getStructureType(structureData.getType())).thenReturn(structureType);
        }
        var type = hasStructure ? structureType : tileType;
        assertThat(fixture.getCover()).isEqualTo(type.getCover());
    }

    @Test
    void getStructure(SeededRng random) {
        var structureData = random.nextStructureData();
        tileData.setStructureData(structureData);
        assertThat(fixture.getStructure()).isEqualTo(new StructureImpl(structureData, fixture, ruleset));
        tileData.setStructureData(null);
        assertThat(fixture.getStructure()).isNull();
    }

    @Test
    void getUnit() {
        assertThat(fixture.getUnit()).isNull();
        when(gameState.getUnits()).then(_ -> Stream.of(unit, anotherUnit));
        when(unit.getLocation()).thenReturn(fixture);
        when(anotherUnit.getLocation()).thenReturn(anotherTile);
        assertThat(fixture.getUnit()).isEqualTo(unit);
    }

    @Test
    void createUnit(SeededRng random) {
        var unitTypeId = random.<UnitTypeId>get();
        when(unitType.getId()).thenReturn(unitTypeId);
        var playerId = random.<PlayerId>get();
        when(player.getId()).thenReturn(playerId);
        fixture.createUnit(unitType, player);
        verify(creator).createUnitData(fixture.getId(), unitTypeId, playerId);
    }

    @Test
    void createStructure(SeededRng random) {
        var structureTypeId = random.<StructureTypeId>get();
        when(structureType.getId()).thenReturn(structureTypeId);
        var playerId = random.<PlayerId>get();
        when(player.getId()).thenReturn(playerId);
        fixture.createStructure(structureType, player);
        verify(creator).createStructureData(fixture.getId(), structureTypeId, playerId);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,1,2,3,4
            1,0,0,1,2
            """)
    void getDistance(int x0, int y0, int x1, int y1, int expected) {
        tileData.setId(new TileId(x0, y0));
        when(anotherTile.getId()).thenReturn(new TileId(x1, y1));
        assertThat(fixture.getDistance(anotherTile)).isEqualTo(expected);
        assertThat(fixture.getDistance(anotherTile)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,1,2,3,4
            1,0,0,1,2
            """)
    void getDistance_WhenTheOtherElementIsNotATile_ThenUsesItsTile(int x0, int y0, int x1, int y1, int expected) {
        when(unit.getTile()).thenReturn(anotherTile);
        tileData.setId(new TileId(x0, y0));
        when(anotherTile.getId()).thenReturn(new TileId(x1, y1));
        assertThat(fixture.getDistance(unit)).isEqualTo(expected);
        assertThat(fixture.getDistance(unit)).isEqualTo(expected);
    }

    @Test
    void getDistance_WhenTheOtherElementIsAUnitInATransport_ThenNaN() {
        assertThat(fixture.getDistance(unit)).isNaN();
    }

    @EnumSource
    @ParameterizedTest
    void step(Direction direction) {
        when(gameState.findTile(tileData.getId().step(direction))).thenReturn(anotherTile);
        assertThat(fixture.step(direction)).isEqualTo(anotherTile);
    }

    @Test
    void area() {
        var ids = tileData.getId().area(1).iterator();
        when(gameState.findTile(ids.next())).thenReturn(null);
        when(gameState.findTile(ids.next())).thenReturn(anotherTile);
        when(gameState.findTile(ids.next())).thenReturn(fixture);
        when(gameState.findTile(ids.next())).thenReturn(yetAnotherTile);
        when(gameState.findTile(ids.next())).thenReturn(null);
        assertThat(fixture.area(1)).containsExactly(anotherTile, fixture, yetAnotherTile);
    }

    @Test
    void getTile() {
        assertThat(fixture.getTile()).isSameAs(fixture);
    }

    @Test
    void testToString() {
        var id = tileData.getId();
        assertThat(fixture).hasToString("Tile[x=%d, y=%d, type=%s]", id.x(), id.y(), tileType);
    }

}
