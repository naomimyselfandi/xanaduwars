package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.GameStateData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.control.DeepClone;

@Mapper(mappingControl = DeepClone.class, uses = CopyMachineHelper.class)
interface CopyMachine {

    @Mapping(target = "id", ignore = true)
    GameStateData copy(GameStateData source);

}
