package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that calculates a multiplier for the damage a unit inflicts.
public record FirepowerQuery(Unit subject, Asset target) implements Query<Double> {

    @Override
    public Double defaultValue() {
        return 1.0;
    }

}
