package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionCleanupEvent;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A rule that handles the standard post-action cleanup.
public record PostActionRule() implements GameRule<ActionCleanupEvent, None> {

    @Override
    public boolean handles(ActionCleanupEvent query, None value) {
        return query.subject() instanceof Unit;
    }

    @Override
    public None handle(ActionCleanupEvent query, None value) {
        ((Unit) query.subject()).canAct(false);
        return None.NONE;
    }

}
