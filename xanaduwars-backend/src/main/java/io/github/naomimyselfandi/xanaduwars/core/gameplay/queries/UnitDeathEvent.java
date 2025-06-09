package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;

/// An event indicating a unit was destroyed.
public record UnitDeathEvent(Unit subject) implements Event {}
