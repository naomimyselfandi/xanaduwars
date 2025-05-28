package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Direction;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.EntryQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.function.Function;

/// An action that causes a transport unit to drop its cargo.
public record DropAction(@Override @NotNull @Valid Name name) implements ActionWithEnumTarget<Unit, Direction> {

    // Currently, all transports can only carry a single unit.
    // If we add a hangarSize stat, we'll need to update this.

    @Override
    public boolean test(Unit unit, Direction direction) {
        // Find the unit to unload, or bail if there isn't one.
        if (unit.cargo().isEmpty()) return false;
        var cargo = unit.cargo().getFirst();
        // Find the destination tile, or bail if there isn't one.
        var destination = unit.tile().flatMap(tile -> tile.step(direction)).orElse(null);
        if (destination == null) return false;
        // Unloading doesn't consume the cargo unit's speed, but we still need
        // to ensure it can enter the destination tile. EntryQuery will return
        // NaN if the tile is blocked or otherwise impassible.
        return !unit.gameState().evaluate(new EntryQuery(cargo, destination)).isNaN();
    }

    @Override
    public Execution execute(Unit unit, Direction direction) {
        // These are both enforced by test() and don't rely on
        // hidden information, so we can assume they're valid.
        var cargo = unit.cargo().getFirst();
        var destination = unit.tile().flatMap(tile -> tile.step(direction)).orElseThrow();
        // Check if hidden information should interrupt this action.
        // Read the comment in test() for context around EntryQuery.
        if (unit.gameState().evaluate(new EntryQuery(cargo, destination)).isNaN()) {
            return Execution.INTERRUPTED;
        }
        // The destination contains either nothing or an appropriate transport unit.
        // .<Node>map(Function.identity()) works around a quirk in the Optional API.
        cargo.location(destination.unit().<Node>map(Function.identity()).orElse(destination));
        return Execution.SUCCESSFUL;
    }

}
