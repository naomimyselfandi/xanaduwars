package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

@Mapper
interface GameStateDtoConverter extends Converter<GameState, GameStateDto> {

    @Mapping(target = "version", source = "ruleset.version")
    @Mapping(target = "activePlayer", source = "activePlayer.id")
    GameStateDto convert(GameState source);

    @Mapping(target = "commander", source = "commander.id")
    @Mapping(target = "active", expression = "java(source.equals(source.getGameState().getActivePlayer()))")
    PlayerDto convert(Player source);

    @Mapping(target = "spell", source = "spell.id")
    SpellSlotDto convert(SpellSlot source);

    @Mapping(target = "owner", source = "owner.id")
    @Mapping(target = "tile", source = "tile.id")
    @Mapping(target = "type", source = "type.id")
    StructureDto convert(Structure source);

    @Mapping(target = "type", source = "type.id")
    TileDto convert(Tile source);

    @Mapping(target = "owner", source = "owner.id")
    @Mapping(target = "tile", expression = "java(source.getTile() == null ? null : source.getTile().getId())")
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

}
