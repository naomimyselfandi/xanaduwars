package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.NodeId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.PlayerId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.control.DeepClone;

@SuppressWarnings("unused")
@Mapper(mappingControl = DeepClone.class, imports = PlayerId.class)
abstract class AbstractGameDataFactory implements GameDataFactory {

    @Override
    @Mapping(target = "id", ignore = true)
    public abstract GameData create(GameData source);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activePlayer", expression = "java(new PlayerId(0))")
    @Mapping(target = "turn", constant = "0")
    @Mapping(target = "version", source = "version")
    public abstract GameData create(MapData source, Version version);

    abstract PlayerData copy(PlayerData source);

    abstract TileData copy(TileData source);

    abstract UnitData copy(UnitData source);

    // MapStruct doesn't assume an identity copy is okay here
    NodeId copy(NodeId source) {
        return source;
    }

}
