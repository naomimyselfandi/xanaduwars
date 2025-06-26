package io.github.naomimyselfandi.xanaduwars.core.gamestate.service;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;

/// A service that copies game state data.
public interface CopyMachine {

    /// Copy some game state data. The copy is not persisted.
    GameStateData copy(GameStateData source);

}
