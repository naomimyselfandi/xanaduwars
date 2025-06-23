package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that calculates an asset's defense rating. When a unit attacks, the
/// defender's defense rating is subtracted from the attacker's firepower rating
/// to produce a damage multiplier.
public record DefenseQuery(Asset subject, Weapon weapon, Unit attacker, boolean counter) implements Query<Double> {

    @Override
    public Double defaultValue() {
        return 0.0;
    }

}
