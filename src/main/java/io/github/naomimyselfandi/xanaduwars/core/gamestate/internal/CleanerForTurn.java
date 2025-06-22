package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Turn;

record CleanerForTurn() implements Cleaner {

    @Override
    public void clean(GameState gameState) {
        var players = gameState.getPlayers();
        var base = gameState.getTurn().ordinal();
        if (gameState.isPassed() || gameState.getActivePlayer().isDefeated()) {
            gameState.setPassed(false);
            var count = players.size();
            var end = base + count;
            for (var i = base + 1; i < end; i++) {
                if (!players.get(i % players.size()).isDefeated()) {
                    gameState.setTurn(new Turn(i));
                    break;
                }
            }
        }
    }

}
