package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record BiFilterUsingTile<S>(@Override @NotNull @Valid BiFilter<S, Tile> filter) implements BiFilterWrapper<S, Unit> {

    @Override
    public boolean test(S subject, Unit target) {
        return target.tile().filter(tile -> filter.test(subject, tile)).isPresent();
    }

    @Override
    public String toString() {
        return "tile." + filter;
    }

}
