package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

/// A query that determines whether an actor is ready to act.
public record ReadyStateQuery(Actor actor) implements SimpleQuery<Boolean> {

    @Override
    public Boolean defaultValue(ScriptRuntime runtime) {
        return true;
    }

}
