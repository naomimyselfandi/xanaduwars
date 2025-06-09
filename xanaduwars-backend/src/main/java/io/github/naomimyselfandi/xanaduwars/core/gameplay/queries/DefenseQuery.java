package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Terrain;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that calculates an asset's defense rating. This is subtracted from
/// the attacker's firepower rating to determine a firepower multiplier.
public record DefenseQuery(Asset subject, Unit attacker) implements Query<Double> {

    @Override
    public Double defaultValue() {
        if (subject instanceof Unit && subject.terrain() instanceof Terrain terrain) {
            return terrain.cover();
        } else {
            return 0.0;
        }
    }

}
