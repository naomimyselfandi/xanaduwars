package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.StructureData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitTypeId;

record CreatorImpl(GameStateData data) implements Creator {

    @Override
    public void createUnitData(NodeId location, UnitTypeId type, PlayerId owner) {
        if (data.getUnitData().stream().anyMatch(it -> it.getLocation().equals(location))) {
            throw new IllegalStateException("%s already has a unit.".formatted(location));
        }
        var unitId = data.getNextUnitId();
        var unitData = new UnitData().setId(unitId).setType(type).setOwner(owner).setLocation(location);
        data.getUnitData().add(unitData);
        data.setNextUnitId(new UnitId(unitId.unitId() + 1));
    }

    @Override
    public void createStructureData(TileId location, StructureTypeId type, PlayerId owner) {
        var tileData = data.tileDataAt(location).orElseThrow();
        if (tileData.getStructureData() == null) {
            var structureData = new StructureData().setType(type).setOwner(owner);
            tileData.setStructureData(structureData);
        } else {
            throw new IllegalStateException("%s already has a structure.".formatted(location));
        }
    }

}
