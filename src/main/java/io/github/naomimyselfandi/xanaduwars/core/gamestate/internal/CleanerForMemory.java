package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;

record CleanerForMemory() implements Cleaner {

    @Override
    public void clean(GameState gameState) {
        for (var player : gameState.getPlayers()) {
            for (var tile : gameState.getTiles().values()) {
                if (player.canSee(tile)) {
                    var type = tile.getStructure().map(Structure::getType).orElse(null);
                    tile.setMemory(player, type);
                }
            }
        }
    }

}
