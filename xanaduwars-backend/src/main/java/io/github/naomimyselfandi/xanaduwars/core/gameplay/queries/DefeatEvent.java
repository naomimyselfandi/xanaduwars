package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Player;

/// A notification of a player's defeat.
public record DefeatEvent(Player subject) implements Event {}
