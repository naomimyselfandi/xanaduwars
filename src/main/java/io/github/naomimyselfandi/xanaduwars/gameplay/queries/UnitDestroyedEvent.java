package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;

/// An event indicating a unit was destroyed.
public record UnitDestroyedEvent(@Override Unit subject) implements NodeDestroyedEvent<Unit>, UnitLeftEvent {

    @Override
    public Node previousLocation() {
        return subject.location();
    }

}
