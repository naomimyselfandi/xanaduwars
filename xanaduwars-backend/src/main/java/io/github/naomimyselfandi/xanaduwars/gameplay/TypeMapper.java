package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface TypeMapper {

    default CommanderId toId(Commander source) {
        return source.id();
    }

    default SpellTypeId toId(SpellType<?> source) {
        return source.id();
    }

    default TileTypeId toId(TileType source) {
        return source.id();
    }

    default UnitTypeId toId(UnitType source) {
        return source.id();
    }

    default Name toName(Type source) {
        return source.name();
    }

    default Name toName(NodeType source) {
        return source.name();
    }

    default Name toName(Commander source) {
        return source.name();
    }

    default Name toName(SpellType<?> source) {
        return source.name();
    }

    default Name toName(TileType source) {
        return source.name();
    }

    default Name toName(UnitType source) {
        return source.name();
    }

    default Name toName(Action<?, ?> source) {
        return source.name();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default @Nullable Name toName(Optional<TileType> source) {
        return source.map(TileType::name).orElse(null);
    }

}
