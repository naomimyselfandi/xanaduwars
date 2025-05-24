package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.GameState;
import io.github.naomimyselfandi.xanaduwars.core.data.GameData;

/// A factory that creates game states.
public interface GameStateFactory {

    /// Create a game state from its low-level data.
    GameState create(GameData gameData);

}
