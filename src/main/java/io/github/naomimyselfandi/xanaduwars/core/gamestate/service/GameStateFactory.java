package io.github.naomimyselfandi.xanaduwars.core.gamestate.service;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;

/// A factory that creates games from low-level game data.
public interface GameStateFactory {

    /// Wrap some low-level game data in a full game state.
    GameState create(GameStateData data);

}
