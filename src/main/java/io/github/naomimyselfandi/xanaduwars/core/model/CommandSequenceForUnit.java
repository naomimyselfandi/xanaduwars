package io.github.naomimyselfandi.xanaduwars.core.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A sequence of commands executed by a unit.
public record CommandSequenceForUnit(int x, int y,
                                     @Unmodifiable List<Command> unitCommands) implements CommandSequence {

    /// A sequence of commands for a unit to execute.
    public CommandSequenceForUnit(int x, int y, List<Command> unitCommands) {
        this.x = x;
        this.y = y;
        this.unitCommands = List.copyOf(unitCommands);
    }

    @Override
    public boolean submit(GameState gameState) throws CommandException {
        return gameState.submitUnitCommand(x, y, unitCommands);
    }

}
