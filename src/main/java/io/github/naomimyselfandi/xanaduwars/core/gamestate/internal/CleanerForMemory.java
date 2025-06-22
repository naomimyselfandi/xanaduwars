package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;

record CleanerForMemory() implements Cleaner {

    @Override
    public void clean(GameState gameState) {
        for (var player : gameState.getPlayers()) {
            for (var tile : gameState.getTiles().values()) {
                if (player.canSee(tile)) {
                    if (tile.getStructure() instanceof Structure structure) {
                        tile.setMemory(player, structure.getType());
                    } else {
                        tile.setMemory(player, null);
                    }
                }
            }
        }
    }

}
