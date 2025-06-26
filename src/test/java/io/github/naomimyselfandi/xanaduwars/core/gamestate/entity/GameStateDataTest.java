package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class GameStateDataTest {

    private GameStateData fixture;
    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new GameStateData();
        this.random = random;
    }

    @Test
    void addStructureData(SeededRng random) {
        var tileId = random.<TileId>get();
        var typeId = random.<StructureTypeId>get();
        var structureData = fixture.createStructure(tileId, typeId);
        var expected = new StructureData().setTypeId(typeId).setHp(Hp.FULL).setIncomplete(false).setPlayerId(null);
        assertThat(structureData).isEqualTo(expected);
        assertThat(fixture.getStructures()).containsEntry(tileId.structureId(), expected);
    }

    @Test
    void addStructureData_WhenTheTileAlreadyHasAStructure_ThenThrows(SeededRng random) {
        var tileId = random.<TileId>get();
        var typeId = random.<StructureTypeId>get();
        var anotherTypeId = random.not(typeId);
        var structureData = fixture.createStructure(tileId, typeId);
        assertThatThrownBy(() -> fixture.createStructure(tileId, anotherTypeId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s already contains a structure.", tileId);
        assertThat(fixture.getStructures()).containsEntry(tileId.structureId(), structureData);
    }

    @Test
    void removeStructureData(SeededRng random) {
        var tileId = random.<TileId>get();
        var typeId = random.<StructureTypeId>get();
        var structureId = tileId.structureId();
        fixture.createStructure(tileId, typeId);
        fixture.removeStructure(structureId);
        assertThat(fixture.getStructures()).doesNotContainKey(structureId);
    }

    @Test
    void addUnitData(SeededRng random) {
        var tileId = random.<TileId>get();
        var typeId = random.<UnitTypeId>get();
        var unitId = new UnitId(0);
        var entry = fixture.createUnit(tileId, typeId);
        var expected = new UnitData()
                .setLocationId(tileId)
                .setTypeId(typeId)
                .setPlayerId(null)
                .setReady(false)
                .setHp(Hp.FULL);
        assertThat(entry).isEqualTo(Map.entry(unitId, expected));
        assertThat(fixture.getUnits()).containsEntry(unitId, expected);
    }

    @Test
    void addUnitData_AssignsEachUnitAUniqueId(SeededRng random) {
        var typeId = random.<UnitTypeId>get();
        var tileId0 = random.<TileId>get();
        var tileId1 = random.not(tileId0);
        var tileId2 = random.not(tileId0, tileId1);
        var entry0 = fixture.createUnit(tileId0, typeId);
        var entry1 = fixture.createUnit(tileId1, typeId);
        var entry2 = fixture.createUnit(tileId2, typeId);
        assertThat(fixture.getUnits()).containsExactly(entry0, entry1, entry2);
        assertThat(fixture.getUnits().keySet()).containsExactly(new UnitId(0), new UnitId(1), new UnitId(2));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2})
    void addUnitData_InitializesTheNextUnitIdIfNeeded(int delta, SeededRng random) {
        var unitId = random.<UnitId>get();
        var unitData = random.<UnitData>get();
        fixture.getUnits().put(unitId, unitData);
        fixture.setNextUnitId(new UnitId(unitId.unitId() + delta));
        var added = fixture.createUnit(random.get(), random.get());
        assertThat(fixture.getUnits()).containsEntry(unitId, unitData).containsEntry(added.getKey(), added.getValue());
        assertThat(fixture.getNextUnitId()).isEqualTo(new UnitId(added.getKey().unitId() + 1));
    }

    @Test
    void addUnitData_WhenTheTileAlreadyHasAUnit_ThenThrows(SeededRng random) {
        var tileId = random.<TileId>get();
        var typeId = random.<UnitTypeId>get();
        var anotherTypeId = random.not(typeId);
        var entry = fixture.createUnit(tileId, typeId);
        assertThatThrownBy(() -> fixture.createUnit(tileId, anotherTypeId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s already contains a unit.", tileId);
        assertThat(fixture.getUnits()).contains(entry);
    }

    @Test
    void removeUnitData(SeededRng random) {
        var tileId = random.<TileId>get();
        var typeId = random.<UnitTypeId>get();
        var unitId = fixture.createUnit(tileId, typeId).getKey();
        fixture.removeUnit(unitId);
        assertThat(fixture.getUnits()).doesNotContainKey(unitId);
    }

    @Test
    void findUnitId(SeededRng random) {
        var typeId = random.<UnitTypeId>get();
        var tileId0 = random.<TileId>get();
        var tileId1 = random.not(tileId0);
        var tileId2 = random.not(tileId0, tileId1);
        var entry0 = fixture.createUnit(tileId0, typeId);
        var entry1 = fixture.createUnit(tileId1, typeId);
        assertThat(fixture.findUnitId(tileId0)).contains(entry0.getKey());
        assertThat(fixture.findUnitId(tileId1)).contains(entry1.getKey());
        assertThat(fixture.findUnitId(tileId2)).isEmpty();
    }

    @Test
    void findUnitById_InitializesIfNeeded() {
        var unitId = random.<UnitId>get();
        var unitData = random.<UnitData>get();
        fixture.getUnits().put(unitId, unitData);
        assertThat(fixture.findUnitId(unitData.getLocationId())).contains(unitId);
    }

    @Test
    void moveUnitData(SeededRng random) {
        var typeId = random.<UnitTypeId>get();
        var tileId0 = random.<TileId>get();
        var tileId1 = random.not(tileId0);
        var entry = fixture.createUnit(tileId0, typeId);
        fixture.moveUnit(entry.getKey(), tileId1);
        assertThat(fixture.findUnitId(tileId0)).isEmpty();
        assertThat(fixture.findUnitId(tileId1)).contains(entry.getKey());
    }

    @Test
    void moveUnitData_WhenTheDestinationAlreadyHasAUnit_ThenThrows(SeededRng random) {
        var typeId = random.<UnitTypeId>get();
        var tileId0 = random.<TileId>get();
        var tileId1 = random.not(tileId0);
        var entry0 = fixture.createUnit(tileId0, typeId);
        var entry1 = fixture.createUnit(tileId1, typeId);
        assertThatThrownBy(() -> fixture.moveUnit(entry0.getKey(), tileId1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s already contains a unit.", tileId1);
        assertThat(fixture.findUnitId(tileId0)).contains(entry0.getKey());
        assertThat(fixture.findUnitId(tileId1)).contains(entry1.getKey());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,1,0,0,true
            3,4,2,3,true
            3,4,3,3,false
            3,4,2,4,false
            """)
    void hasValidStructureIds(int width, int height, int x, int y, boolean valid) {
        setTiles(width, height);
        fixture.getStructures().put(new StructureId(x, y), random.get());
        assertThat(fixture.hasValidStructureIds()).isEqualTo(valid);
    }

    @ParameterizedTest
    @CsvSource({
            "0,0", // Invalid, but rejected by @NotEmpty, not here
            "1,1",
            "2,2",
            "5,4"
    })
    void hasValidTileIds_WhenTheIdsFormACompleteRectangle_ThenTrue(int width, int height) {
        setTiles(width, height);
        assertThat(fixture.hasValidTileIds()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,2
            5,4
            """)
    void hasValidTileIds_WhenTheIdsDoNotFormACompleteRectangle_ThenFalse(int width, int height) {
        setTiles(width, height);
        fixture.getTiles().remove(random.pick(fixture.getTiles().keySet()));
        assertThat(fixture.hasValidTileIds()).isFalse();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,,true
            0,0,false
            1,0,true
            1,1,false
            2,1,true
            """)
    void hasValidUnitIds(int nextUnitId, @Nullable Integer unitId, boolean valid) {
        fixture.setNextUnitId(new UnitId(nextUnitId));
        if (unitId != null) {
            fixture.getUnits().put(new UnitId(unitId), random.get());
        }
        assertThat(fixture.hasValidUnitIds()).isEqualTo(valid);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,0,true
            2,1,true
            2,2,false
            3,2,true
            3,3,false
            """)
    void hasValidStructureOwners(int players, int owner, boolean valid) {
        setPlayers(players);
        var structure = random.<StructureData>get().setPlayerId(new PlayerId(owner));
        fixture.getStructures().put(random.get(), structure);
        assertThat(fixture.hasValidStructureOwners()).isEqualTo(valid);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,0,true
            2,1,true
            2,2,false
            3,2,true
            3,3,false
            """)
    void hasValidUnitOwners(int players, int owner, boolean valid) {
        setPlayers(players);
        var unit = random.<UnitData>get().setPlayerId(new PlayerId(owner));
        fixture.getUnits().put(random.get(), unit);
        assertThat(fixture.hasValidUnitOwners()).isEqualTo(valid);
    }


    @Test
    void getTurn(SeededRng random) {
        assertThat(fixture.getTurn()).isEqualTo(new Turn(0));
        var turn = random.<Turn>get();
        assertThat(fixture.setTurn(turn)).isSameAs(fixture);
        assertThat(fixture.getTurn()).isEqualTo(turn);
    }

    @Test
    void getNextUnitId(SeededRng random) {
        assertThat(fixture.getNextUnitId()).isEqualTo(new UnitId(0));
        var nextUnitId = random.<UnitId>get();
        assertThat(fixture.setNextUnitId(nextUnitId)).isSameAs(fixture);
        assertThat(fixture.getNextUnitId()).isEqualTo(nextUnitId);
    }

    private void setTiles(int width, int height) {
        for (var x = 0; x < width; x++) {
            for (var y = 0; y < height; y++) {
                fixture.getTiles().put(new TileId(x, y), random.get());
            }
        }
    }

    private void setPlayers(int players) {
        fixture.setPlayers(IntStream.range(0, players).mapToObj(_ -> random.<PlayerData>get()).toList());
    }

}
