package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Node;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

/// A query that checks if a node is perceived by a unit.
public record IsNodePerceivedByUnitQuery(Node target, Unit observer) implements SimpleQuery<Boolean> {

    @Override
    public Boolean defaultValue(ScriptRuntime runtime) {
        return observer.getDistance(target) <= observer.getPerception();
    }

}
