package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

/// A query that checks if one unit can board another.
public record CanBoardQuery(Unit passenger, Unit transport) implements SimpleQuery<Boolean> {

    @Override
    public Boolean defaultValue(ScriptRuntime runtime) {
        return passenger.getOwner().equals(transport.getOwner())
                && (transport.getUnit() == null)
                && transport.getHangar().stream().anyMatch(passenger.getTags()::contains);
    }

}
