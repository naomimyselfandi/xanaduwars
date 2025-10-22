package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.core.model.Player;

/// A service that copies game states.
public interface CopyMachine {

    /// Copy a game state. The copy is identical to the source, except that any
    /// event listeners attached to the source are not copied. Both game states
    /// are independent after the copy is created, and changes to one are never
    /// reflected in the other.
    GameState createCopy(GameState source);

    /// Copy a game state, omitting information hidden to the given player.
    GameState createRedactedCopy(GameState source, Player viewpoint);

}
