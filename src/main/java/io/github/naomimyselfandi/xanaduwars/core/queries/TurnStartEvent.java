package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Player;

/// An event indicating a player
public record TurnStartEvent(@Override Player subject) implements SubjectQuery.Event {}
