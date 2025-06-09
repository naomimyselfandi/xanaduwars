package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.TileData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.UnitData;
import org.mapstruct.Mapper;

@Mapper
@SuppressWarnings("unused")
interface CopyMachineHelper {

    PlayerData copy(PlayerData source);

    TileData copy(TileData source);

    UnitData copy(UnitData source);

    default NodeId copy(NodeId source) {
        return source;
    }

}
