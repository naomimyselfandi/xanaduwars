package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;

/// A service that create game states.
public interface GameStateFactory {

    /// Create a game state.
    /// @param width The width of the playable area.
    /// @param height The height of the playable area.
    /// @param players The number of players.
    /// @param version The version of the game to use.
    GameState create(int width, int height, int players, Version version);

}
