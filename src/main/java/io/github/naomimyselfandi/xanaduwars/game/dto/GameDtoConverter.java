package io.github.naomimyselfandi.xanaduwars.game.dto;

import io.github.naomimyselfandi.xanaduwars.ConversionServiceAdapter;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.SequencedMap;

@Mapper(uses = ConversionServiceAdapter.class)
interface GameDtoConverter extends Converter<Game, GameDto> {

    @Override
    @Mapping(target = "host", source = "host.id")
    @Mapping(target = "turnCount", expression = "java(source.getGameStateData().getTurn().ordinal() + 1)")
    GameDto convert(Game source);

    @Mapping(target = "id", ignore = true)
    PlayerSlotDto convert(PlayerSlot source);

    default List<PlayerSlotDto> convert(SequencedMap<PlayerId, PlayerSlot> source) {
        return source
                .entrySet()
                .stream()
                .map(entry -> convert(entry.getValue()).setId(entry.getKey()))
                .toList();
    }

}
