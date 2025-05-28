package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.query.Query;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;

import java.util.Comparator;
import java.util.Objects;

/// A query that determines the speed cost for a unit to enter a tile. A result
/// of {@code NaN} indicates that the unit cannot enter the tile. Rules should
/// neither assume nor enforce that the unit is adjacent to the tile.
public record EntryQuery(@Override Unit subject, @Override Tile target) implements Query<Double> {

    @Override
    public boolean shouldShortCircuit(Double value) {
        return value.isNaN();
    }

    @Override
    public Double defaultValue() {
        return subject
                .tags()
                .stream()
                .map(target.movementTable()::get)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(Double.NaN);
    }

}
