package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;

/// An event indicating a unit's owner is starting their turn.
public record TurnStartEventForUnit(@Override Unit subject) implements SubjectQuery.Event<Unit> {}
