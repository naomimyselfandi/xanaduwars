package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;

/// A service that can evaluate commands in a game.
public interface CommandProcessor {

    /// Evaluate a command in a game. The command may either succeed, fail, or
    /// be rejected. A rejection is indicated with an exception, and means that
    /// the command was structurally invalid in some way, such as referring to a
    /// nonexistent target or naming a nonexistent action, or that it violated a
    /// game rule without involving hidden information. Failure means that the
    /// command violated a game rule, but that the player did not have enough
    /// information to know it would; this typically occurs when a unit is
    /// commanded to move through a tile containing a previously unseen enemy
    /// unit.
    Result process(GameState gameState, CommandDto command) throws ConflictException;

}
