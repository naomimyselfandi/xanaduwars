package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.History;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.StructureData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitTypeId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class CreatorImplTest {

    private GameStateData data;
    private CreatorImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        data = new GameStateData().tileData(List.of(
                random.nextTileData().id(new TileId(0, 0)).structureData(null),
                random.nextTileData().id(new TileId(1, 0)).structureData(null),
                random.nextTileData().id(new TileId(2, 0)).structureData(null),
                random.nextTileData().id(new TileId(3, 0)).structureData(null),
                random.nextTileData().id(new TileId(0, 1)).structureData(null),
                random.nextTileData().id(new TileId(1, 1)).structureData(null),
                random.nextTileData().id(new TileId(2, 1)).structureData(null),
                random.nextTileData().id(new TileId(3, 1)).structureData(null),
                random.nextTileData().id(new TileId(0, 2)).structureData(null),
                random.nextTileData().id(new TileId(1, 2)).structureData(null),
                random.nextTileData().id(new TileId(2, 2)).structureData(null),
                random.nextTileData().id(new TileId(3, 2)).structureData(null)
        ));
        fixture = new CreatorImpl(data);
    }

    @Test
    void createUnitData(SeededRng random) {
        var unitId = random.<UnitId>get();
        var location = random.<NodeId>get();
        var type = random.<UnitTypeId>get();
        var owner = random.<PlayerId>get();
        data.nextUnitId(unitId);
        fixture.createUnitData(location, type, owner);
        assertThat(data.unitData()).singleElement().isEqualTo(new UnitData()
                .id(unitId)
                .type(type)
                .owner(owner)
                .hp(100)
                .location(location)
                .history(History.NONE));
        assertThat(data.nextUnitId()).isEqualTo(new UnitId(unitId.unitId() + 1));
    }

    @Test
    void createUnitData_WhenTheLocationAlreadyHasAUnit_ThenThrows(SeededRng random) {
        var location = random.<NodeId>get();
        fixture.createUnitData(location, random.get(), random.get());
        assertThatThrownBy(() -> fixture.createUnitData(location, random.get(), random.get()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s already has a unit.", location);
    }

    @RepeatedTest(2)
    void createStructureData(SeededRng random) {
        var location = new TileId(random.nextInt(4), random.nextInt(3));
        var type = random.<StructureTypeId>get();
        var owner = random.<PlayerId>get();
        fixture.createStructureData(location, type, owner);
        assertThat(data.tileDataAt(location).orElseThrow().structureData()).isEqualTo(new StructureData()
                .type(type)
                .owner(owner)
                .hp(0)
                .complete(false));
    }

    @Test
    void createStructureData_WhenTheLocationAlreadyHasAStructure_ThenThrows(SeededRng random) {
        var location = new TileId(3, 2);
        fixture.createStructureData(location, random.get(), random.get());
        assertThatThrownBy(() -> fixture.createStructureData(location, random.get(), random.get()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s already has a structure.", location);
    }

}
