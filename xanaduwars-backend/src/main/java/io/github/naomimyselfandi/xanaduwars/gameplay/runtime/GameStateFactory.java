package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameData;

/// A factory that creates game states.
public interface GameStateFactory {

    /// Create a game state from its low-level data.
    GameState create(GameData gameData);

}
