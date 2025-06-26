package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.GameStateDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.game.dto.GameDto;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
class GameViewerImpl implements GameViewer {

    private final GameStateFactory gameStateFactory;
    private final Converter<Game, GameDto> gameDtoConverter;
    private final Converter<GameState, GameStateDto> gameStateDtoConverter;

    @Override
    @PreAuthorize("@gameGuard.canViewMetadata(#game)")
    public GameDto getMetadata(Game game) {
        return Objects.requireNonNull(gameDtoConverter.convert(game));
    }

    @Override
    @PreAuthorize("@gameGuard.canView(#game, #playerId)")
    public ChoiceDto getChoices(Game game, PlayerId playerId) {
        var data = game.getGameStateData().getPlayers().get(playerId.playerId());
        return new ChoiceDto().setCommander(data.getCommanderId()).setSpells(data.getChosenSpells().getSpellIds());
    }

    @Override
    @PreAuthorize("@gameGuard.canView(#game, #playerId) && #game.status.name != 'PENDING'")
    public GameStateDto view(Game game, @Nullable PlayerId playerId) {
        var gameState = gameStateFactory.create(game.getGameStateData());
        if (playerId != null) {
            var player = gameState.getPlayers().get(playerId.playerId());
            gameState = gameState.limitedTo(player);
        }
        return Objects.requireNonNull(gameStateDtoConverter.convert(gameState));
    }

}
