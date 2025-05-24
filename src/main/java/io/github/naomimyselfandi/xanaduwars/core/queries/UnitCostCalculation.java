package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Resource;
import io.github.naomimyselfandi.xanaduwars.core.Tile;
import io.github.naomimyselfandi.xanaduwars.core.UnitType;

/// A query that calculates the cost to deploy a unit.
public record UnitCostCalculation(@Override Tile subject, UnitType type, Resource resource)
        implements SubjectQuery<Integer> {}
