package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A sequence of commands executed by the active player themselves.
public record CommandSequenceForSelf(@Unmodifiable List<Command> selfCommands) implements CommandSequence {

    /// A sequence of commands for a player to execute.
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CommandSequenceForSelf(List<Command> selfCommands) {
        this.selfCommands = List.copyOf(selfCommands);
    }

    @Override
    public boolean submit(GameState gameState) throws CommandException {
        return gameState.submitPlayerCommand(selfCommands);
    }

}
