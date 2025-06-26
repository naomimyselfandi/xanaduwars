package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.stream.Collectors;

@Mapper(imports = {Commander.class, Spell.class})
interface GameStateDtoConverter extends Converter<GameState, GameStateDto> {

    @Mapping(target = "version", source = "ruleset.version")
    @Mapping(target = "activePlayer", source = "activePlayer.id")
    GameStateDto convert(GameState source);

    @Mapping(target = "commander", expression = "java(source.getCommander().map(Commander::getId).orElse(null))")
    @Mapping(target = "active", expression = "java(source.equals(source.getGameState().getActivePlayer()))")
    PlayerDto convert(Player source);

    @Mapping(target = "spell", expression = "java(source.getSpell().map(Spell::getId).orElse(null))")
    SpellSlotDto convert(SpellSlot source);

    @Mapping(target = "owner", expression = "java(source.getOwner().map(Player::getId).orElse(null))")
    @Mapping(target = "tile", expression = "java(source.getTile().map(Tile::getId).orElse(null))")
    @Mapping(target = "type", source = "type.id")
    StructureDto convert(Structure source);

    @Mapping(target = "type", source = "type.id")
    TileDto convert(Tile source);

    @Mapping(target = "owner", expression = "java(source.getOwner().map(Player::getId).orElse(null))")
    @Mapping(target = "tile", expression = "java(source.getTile().map(Tile::getId).orElse(null))")
    @Mapping(target = "type", source = "type.id")
    UnitDto convert(Unit source);

    default List<List<TileDto>> convert(SortedMap<TileId, Tile> tiles) {
        return tiles
                .values()
                .stream()
                .map(this::convert)
                .collect(Collectors.groupingBy(tile -> tile.getId().y()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default @Nullable UnitDto convertOptionalUnit(Optional<Unit> source) {
        return source.map(this::convert).orElse(null);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default @Nullable StructureDto convertOptionalStructure(Optional<Structure> source) {
        return source.map(this::convert).orElse(null);
    }

}
