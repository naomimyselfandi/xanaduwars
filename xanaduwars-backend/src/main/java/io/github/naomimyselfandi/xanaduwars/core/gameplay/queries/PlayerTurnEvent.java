package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Player;

/// An event that indicates a player is beginning their turn.
public record PlayerTurnEvent(Player subject) implements Event {}
