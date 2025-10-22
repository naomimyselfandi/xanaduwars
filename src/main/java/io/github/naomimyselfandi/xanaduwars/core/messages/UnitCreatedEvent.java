package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;

/// An event indicating that a unit was created.
@NotCovered // Trivial
public record UnitCreatedEvent(Unit unit) implements SimpleEvent {}
