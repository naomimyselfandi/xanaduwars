package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;

/// An event notifying an actor to perform any start-of-turn activities.
@NotCovered // Trivial
public record TurnStartedEvent(Actor actor) implements SimpleEvent {}
