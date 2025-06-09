package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a player. A player's ID is equal to their position in the
/// turn order.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record PlayerId(@PositiveOrZero int playerId) implements ActorId {}
