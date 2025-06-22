package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that calculates a unit's base speed.
public record SpeedQuery(Unit subject) implements Query<Integer> {

    @Override
    public Integer defaultValue() {
        return subject.getType().getSpeed();
    }

}
