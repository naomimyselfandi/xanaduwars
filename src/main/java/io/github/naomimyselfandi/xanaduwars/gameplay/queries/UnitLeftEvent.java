package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;

/// An event indicating a unit left a location. This reports both movement and
/// destruction; use [UnitMovedEvent] to capture only movement.
public interface UnitLeftEvent extends SubjectQuery.Event<Unit> {

    /// The location the unit left.
    Node previousLocation();

}
