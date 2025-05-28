package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Player;

/// An event indicating a player started their turn.
public record TurnStartEvent(@Override Player subject) implements SubjectQuery.Event<Player> {}
