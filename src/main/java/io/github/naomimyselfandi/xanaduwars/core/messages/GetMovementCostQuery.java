package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

import java.util.Objects;

/// A query that gets the movement cost a unit would pay to enter a tile.
public record GetMovementCostQuery(Unit unit, Tile tile) implements SimpleQuery<Double> {

    @Override
    public Double defaultValue(ScriptRuntime runtime) {
        return unit
                .getTags()
                .stream()
                .map(tile.getMovementTable()::get)
                .filter(Objects::nonNull)
                .reduce(Double.POSITIVE_INFINITY, Double::min);
    }

}
