package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;

/// A query that calculates the cost to deploy a unit.
public record UnitCostCalculation(@Override Tile subject, UnitType type, Resource resource)
        implements SubjectQuery<Integer, Tile> {

    @Override
    public Integer defaultValue() {
        return type.costs().getOrDefault(resource, 0);
    }

}
