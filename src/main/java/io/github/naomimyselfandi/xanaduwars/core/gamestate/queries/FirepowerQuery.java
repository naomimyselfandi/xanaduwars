package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that calculates a unit's firepower rating. When a unit attacks, the
/// defender's defense rating is subtracted from the attacker's firepower rating
/// to produce a damage multiplier.
public record FirepowerQuery(Unit subject, Weapon weapon, Asset target, boolean counter) implements Query<Double> {

    @Override
    public Double defaultValue() {
        return 1.0;
    }

}
