package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;

/// An event that indicates a unit was destroyed.
public record UnitDestructionEvent(Unit subject) implements SimpleEvent {}
