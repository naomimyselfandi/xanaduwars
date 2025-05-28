package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;

/// An event indicating a unit moved to a new location.
public record UnitMovedEvent(@Override Unit subject, Node previousLocation) implements UnitLeftEvent {}
