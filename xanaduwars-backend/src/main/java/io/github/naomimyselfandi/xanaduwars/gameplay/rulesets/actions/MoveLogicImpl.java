package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.EntryQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Path;

enum MoveLogicImpl implements MoveLogic {

    MOVE_LOGIC;

    @Override
    public Execution execute(Path path, Unit unit) {
        // Unpack these values for convenience/performance.
        var gameState = unit.gameState();
        var steps = path.steps();
        var tags = unit.tags();
        var speedLimit = (double) unit.speed();
        // Accumulator.
        var speedUsed = 0.0;
        // Process each step.
        for (var tile : steps) {
            // Calculate the speed cost for this part of the path. A speed cost
            // of NaN indicates a non-speed-related interruption, typically an
            // unseen enemy unit on the path.
            speedUsed += gameState.evaluate(new EntryQuery(unit, tile));
            if (speedUsed <= speedLimit /* Implicit NaN check */) {
                // We're allowed to move through friendly units, so there might
                // be a unit on this tile. Otherwise, enter the tile so we stop
                // in the right place if we're interrupted Later.
                if (tile.unit().isEmpty()) {
                    unit.location(tile);
                }
            } else {
                // Our movement was interrupted by hidden information. This is
                // typically an unseen enemy unit, but it's also possible for a
                // cost to be higher than expected, perhaps because a structure
                // was destroyed.
                return Execution.INTERRUPTED;
            }
        }
        // If there's a unit on the final tile, we know it's a transport that
        // can carry our unit. If it weren't, we'd have already been rejected
        // by test() (if we could see the unit) or the last EntryQuery (if we
        // couldn't). If this isn't true somehow, unit.location() will throw.
        if (steps.getLast().unit().orElse(null) instanceof Unit transport) {
            unit.location(transport);
        }
        // Report success, along with any rules that fired along the way.
        return Execution.SUCCESSFUL;
    }

}
