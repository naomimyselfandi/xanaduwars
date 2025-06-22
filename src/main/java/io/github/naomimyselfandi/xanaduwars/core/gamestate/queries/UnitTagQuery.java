package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

import java.util.Set;

/// A query that calculates a unit's tag set.
public record UnitTagQuery(Unit subject) implements Query<Set<UnitTag>> {

    @Override
    public Set<UnitTag> defaultValue() {
        return subject.getType().getTags();
    }

}
