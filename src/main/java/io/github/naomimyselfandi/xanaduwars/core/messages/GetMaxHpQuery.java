package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

/// A query that gets a unit's maximum HP.
public record GetMaxHpQuery(Unit unit) implements SimpleQuery<Integer> {

    @Override
    public Integer defaultValue(ScriptRuntime runtime) {
        return unit.getType().getMaxHp();
    }

}
