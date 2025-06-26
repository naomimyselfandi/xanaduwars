package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.CopyMachine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.control.DeepClone;
import org.mapstruct.control.MappingControl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Mapper(mappingControl = DeepClone.class)
interface CopyMachineDetails extends CopyMachine {

    @Retention(RetentionPolicy.CLASS)
    @MappingControl(MappingControl.Use.DIRECT)
    @interface Direct {}

    @Override
    @Mapping(target = "id", mappingControl = Direct.class)
    @Mapping(target = "turn", mappingControl = Direct.class)
    @Mapping(target = "version", mappingControl = Direct.class)
    GameStateData copy(GameStateData source);

    @Mapping(target = "commanderId", mappingControl = Direct.class)
    @Mapping(target = "team", mappingControl = Direct.class)
    PlayerData copy(PlayerData source);

    @Mapping(target = "hp", mappingControl = Direct.class)
    @Mapping(target = "playerId", mappingControl = Direct.class)
    @Mapping(target = "typeId", mappingControl = Direct.class)
    StructureData copy(StructureData source);

    @Mapping(target = "typeId", mappingControl = Direct.class)
    TileData copy(TileData source);

    @Mapping(target = "hp", mappingControl = Direct.class)
    @Mapping(target = "locationId", mappingControl = Direct.class)
    @Mapping(target = "playerId", mappingControl = Direct.class)
    @Mapping(target = "typeId", mappingControl = Direct.class)
    UnitData copy(UnitData source);

}
