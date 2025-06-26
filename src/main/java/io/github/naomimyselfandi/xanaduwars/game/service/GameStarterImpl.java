package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Team;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.CleanupEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.TurnStartEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
class GameStarterImpl implements GameStarter {

    private final GameStateFactory gameStateFactory;

    @Override
    @PreAuthorize("@authService.id == #game.host.id")
    @Transactional(propagation = Propagation.MANDATORY)
    public void start(Game game) throws ConflictException {
        var gameState = gameStateFactory.create(game.getGameStateData());
        ensureGameIsReadyToStart(gameState, game);
        defeatAbsentPlayers(gameState, game);
        initializeMemory(gameState);
        startFirstTurn(gameState, game);
        game.setStatus(Game.Status.ONGOING);
    }

    private static void ensureGameIsReadyToStart(GameState gameState, Game game) throws ConflictException {
        var teams = new HashSet<Team>();
        var slots = game.getPlayerSlots();
        for (var player : gameState.getPlayers()) {
            if (slots.containsKey(player.getId())) {
                if (player.getCommander().isEmpty()) {
                    throw new ConflictException("Cannot start a game before all players have chosen a commander.");
                }
                teams.add(player.getTeam());
            }
        }
        if (teams.size() < 2) {
            throw new ConflictException("Cannot start a game without at least two teams present.");
        }
    }

    private static void defeatAbsentPlayers(GameState gameState, Game game) {
        for (var player : gameState.getPlayers()) {
            if (!game.getPlayerSlots().containsKey(player.getId())) {
                player.defeat();
            }
        }
    }

    private static void initializeMemory(GameState gameState) {
        for (var player : gameState.getPlayers()) {
            for (var structure : gameState.getStructures().values()) {
                structure.getTile().orElseThrow().setMemory(player, structure.getType());
            }
        }
    }

    private void startFirstTurn(GameState gameState, Game game) {
        var activePlayer = gameState.getActivePlayer();
        if (game.getPlayerSlots().containsKey(activePlayer.getId())) {
            gameState.evaluate(new TurnStartEvent(activePlayer));
        } else {
            gameState.evaluate(new CleanupEvent());
        }
    }

}
