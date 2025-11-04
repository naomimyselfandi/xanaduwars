package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.messages.CanBoardQuery;
import io.github.naomimyselfandi.xanaduwars.core.messages.CanPassOverQuery;
import io.github.naomimyselfandi.xanaduwars.core.messages.GetMovementCostQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;

import java.util.List;
import java.util.Objects;

/// A target specifier for movement paths. This hard-codes a few assumptions
/// about how movement works during gameplay, which is generally avoided, but
/// movement is both foundational and complicated, so this is an exception.
enum TargetOfMovementPath implements TargetOfPath {

    MOVEMENT_PATH;

    @Override
    public double getCapacity(Unit unit) {
        return unit.getSpeed();
    }

    @Override
    public double getCapacityUsed(Unit unit, List<Tile> path) {
        var gameState = unit.getGameState();
        // If there's a unit on this path that our unit can't move over, and the
        // unit's owner is aware of it, reject the movement. If the unit's owner
        // isn't aware of it, let the movement proceed; the unit will get stuck,
        // but that's an intended part of gameplay.
        var obstacle = path
                .stream()
                .map(Tile::getUnit)
                .filter(Objects::nonNull)
                .filter(unit.getOwner()::perceives)
                .anyMatch(it -> !gameState.evaluate(new CanPassOverQuery(unit, it)));
        return obstacle ? Double.POSITIVE_INFINITY : path
                .stream()
                .mapToDouble(tile -> gameState.evaluate(new GetMovementCostQuery(unit, tile)))
                .sum();
    }

    @Override
    public boolean doCustomValidation(Unit unit, List<Tile> path) {
        if (path.isEmpty()) {
            return false;
        }
        // If there's a unit at the end of the path, we need to be able to board
        // it, not just pass over it. As above, if the player isn't aware of the
        // obstacle, ignore it and let the unit get stuck.
        var obstacle = path.getLast().getUnit();
        if (obstacle == null || !unit.getOwner().perceives(obstacle)) {
            return true;
        } else {
            return unit.getGameState().evaluate(new CanBoardQuery(unit, obstacle));
        }
    }

}
