package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;

/// An event indicating that a player's turn is beginning.
public record TurnStartEvent(Player subject) implements SimpleEvent {}
