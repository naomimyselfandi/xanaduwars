package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;

/// An event indicating that a player's available resources changed.
public record ResourceEvent(Player subject) implements SimpleEvent {}
