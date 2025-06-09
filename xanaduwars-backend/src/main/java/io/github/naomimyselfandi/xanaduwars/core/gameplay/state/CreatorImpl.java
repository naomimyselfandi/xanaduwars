package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.StructureData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitTypeId;

record CreatorImpl(GameStateData data) implements Creator {

    @Override
    public void createUnitData(NodeId location, UnitTypeId type, PlayerId owner) {
        if (data.unitData().stream().anyMatch(it -> it.location().equals(location))) {
            throw new IllegalStateException("%s already has a unit.".formatted(location));
        }
        var unitId = data.nextUnitId();
        var unitData = new UnitData().id(unitId).type(type).owner(owner).location(location);
        data.unitData().add(unitData);
        data.nextUnitId(new UnitId(unitId.unitId() + 1));
    }

    @Override
    public void createStructureData(TileId location, StructureTypeId type, PlayerId owner) {
        var tileData = data.tileDataAt(location).orElseThrow();
        if (tileData.structureData() == null) {
            var structureData = new StructureData().type(type).owner(owner);
            tileData.structureData(structureData);
        } else {
            throw new IllegalStateException("%s already has a structure.".formatted(location));
        }
    }

}
