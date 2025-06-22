package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that determines an action's minimum range.
public record MinRangeQuery(Unit subject, Action action, TargetSpec targetSpec) implements Query<Integer> {

    @Override
    public Integer defaultValue() {
        return targetSpec.minRange();
    }

}
