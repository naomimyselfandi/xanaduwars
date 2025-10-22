package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

/// A query that gets a unit's speed.
public record GetSpeedQuery(Unit unit) implements SimpleQuery<Integer> {

    @Override
    public Integer defaultValue(ScriptRuntime runtime) {
        return unit.getType().getSpeed();
    }

}
