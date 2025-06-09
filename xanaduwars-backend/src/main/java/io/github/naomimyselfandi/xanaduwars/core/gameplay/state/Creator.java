package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitTypeId;

interface Creator {

    void createUnitData(NodeId location, UnitTypeId type, PlayerId owner);

    void createStructureData(TileId location, StructureTypeId type, PlayerId owner);

}
