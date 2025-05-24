package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Node;
import io.github.naomimyselfandi.xanaduwars.core.Unit;

/// An event indicating a unit moved to a new location.
public record UnitMovedEvent(@Override Unit subject, Node previousLocation) implements SubjectQuery.Event {}
