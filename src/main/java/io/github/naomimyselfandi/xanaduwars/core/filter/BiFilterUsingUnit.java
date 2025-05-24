package io.github.naomimyselfandi.xanaduwars.core.filter;

import io.github.naomimyselfandi.xanaduwars.core.Tile;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record BiFilterUsingUnit<S>(@Override @NotNull @Valid BiFilter<S, Unit> filter) implements BiFilterWrapper<S, Tile> {

    @Override
    public boolean test(S subject, Tile target) {
        return target.unit().filter(unit -> filter.test(subject, unit)).isPresent();
    }

    @Override
    public String toString() {
        return "unit." + filter;
    }

}
