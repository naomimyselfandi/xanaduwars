package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Player;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;

/// An event indicating that a player was defeated.
@NotCovered // Trivial
public record DefeatedEvent(Player player) implements SimpleEvent {}
