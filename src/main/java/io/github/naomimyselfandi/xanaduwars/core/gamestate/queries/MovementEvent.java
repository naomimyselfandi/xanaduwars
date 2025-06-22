package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Node;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;

/// An event that indicates a unit moved.
public record MovementEvent(Unit subject, Node previousLocation) implements SimpleEvent {}
