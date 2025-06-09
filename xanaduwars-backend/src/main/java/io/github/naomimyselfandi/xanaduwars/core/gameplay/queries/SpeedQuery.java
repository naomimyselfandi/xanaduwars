package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that calculates a unit's base speed.
public record SpeedQuery(Unit subject) implements Query<Integer> {

    @Override
    public Integer defaultValue() {
        return subject.type().speed();
    }

}
