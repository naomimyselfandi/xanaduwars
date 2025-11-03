package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Ability;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

/// A query that determines whether an actor can use an ability.
public record PreflightQuery(Actor actor, Ability ability) implements SimpleQuery<Boolean> {

    @Override
    public Boolean defaultValue(ScriptRuntime runtime) {
        return true;
    }

}
