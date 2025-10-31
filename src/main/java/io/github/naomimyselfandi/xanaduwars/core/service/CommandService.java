package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandSequence;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;

/// A service for submitting commands in a game state.
public interface CommandService {

    /// Submit a command sequence to a game state. This internally delegates to
    /// [CommandSequence#submit(GameState)], after applying protections against
    /// certain exploits that a game state can't easily deal with itself.
    void submit(GameState gameState, CommandSequence commandSequence) throws CommandException;

}
