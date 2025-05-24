package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Tile;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.Query;

/// A query that determines the speed cost for a unit to enter a tile. A result
/// of {@code NaN} indicates that the unit cannot enter the tile. Rules should
/// neither assume nor enforce that the unit is adjacent to the tile.
public record EntryQuery(@Override Unit subject, @Override Tile target) implements Query<Double> {

    @Override
    public boolean shouldShortCircuit(Double value) {
        return value.isNaN();
    }

}
