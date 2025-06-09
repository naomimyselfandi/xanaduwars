package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.GameStateData;

interface GameStateHelper {

    void redact(GameState gameState, GameStateData data, Player player);

    void updateMemory(GameState gameState, GameStateData data);

    void cleanup(GameState gameState, GameStateData data);

    void pass(GameState gameState, GameStateData data);

}
