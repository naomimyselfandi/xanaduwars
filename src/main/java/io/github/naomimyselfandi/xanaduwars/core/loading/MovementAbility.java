package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.messages.CanBoardQuery;
import io.github.naomimyselfandi.xanaduwars.core.messages.CanPassOverQuery;
import io.github.naomimyselfandi.xanaduwars.core.messages.GetMovementCostQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;

import java.util.List;
import java.util.Objects;

final class MovementAbility extends AbstractPathAbility {

    private static final Cost FREE = new Cost(0, 0, 0);

    @Override
    public Cost getCost(Actor actor, Object target) {
        return FREE;
    }

    @Override
    double getCapacity(Unit unit) {
        return unit.getSpeed();
    }

    @Override
    double getCapacityUsed(Unit unit, List<Tile> path) {
        var gameState = unit.getGameState();
        // If there's a unit on this path that our unit can't move over, and the
        // unit's owner is aware of it, reject the movement. If the unit's owner
        // isn't aware of it, let the movement proceed; the unit will get stuck,
        // but that's an intended part of gameplay (see execute() below).
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
    boolean validate(Unit unit, List<Tile> path) {
        if (path.isEmpty()) {
            return false;
        }
        // If there's a unit at the end of the path, we need to be able to board
        // it, not just pass over it. As above, if the player isn't aware of the
        // obstacle, ignore it and let the unit get stuck (see execute() below).
        var obstacle = path.getLast().getUnit();
        if (obstacle == null || !unit.getOwner().perceives(obstacle)) {
            return true;
        } else {
            return unit.getGameState().evaluate(new CanBoardQuery(unit, obstacle));
        }
    }

    @Override
    public boolean execute(Unit unit, List<Tile> path) {
        var obstacle = (Unit) null;
        var gameState = unit.getGameState();
        for (var tile : path) {
            // If this tile is empty, enter it. Otherwise, if the moving unit
            // can't pass over the unit already on this tile, it "gets stuck"
            // and the action is interrupted.
            obstacle = tile.getUnit();
            if (obstacle == null) {
                unit.setLocation(tile);
            } else if (!gameState.evaluate(new CanPassOverQuery(unit, obstacle))) {
                return false;
            }
        }
        // If the very last tile contained a unit, it needs to be a transport
        // which the moving unit can board, or the moving unit gets stuck.
        if (obstacle == null) {
            return true;
        } else if (gameState.evaluate(new CanBoardQuery(unit, obstacle))) {
            unit.setLocation(obstacle);
            return true;
        } else {
            return false;
        }
    }

}
