package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Commander;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.stream.Stream;

/// A player in a game state.
public non-sealed interface Player extends Actor {

    /// This player's ID. This corresponds directly to their position in the
    /// turn order: the first player's ID is zero, the second player's ID is
    /// one, and so on.
    PlayerId id();

    /// This player's team. In a one-on-one or free-for-all game, each player is
    /// simply assigned a unique team, typically derived from their player ID.
    Team team();

    /// The commander this player is playing as.
    Commander commander();

    /// This player's spell slots.
    @Unmodifiable List<SpellSlot> spellSlots();

    /// This player's units in creation order.
    Stream<Unit> units();

    /// This player's structures in tile order.
    Stream<Structure> structures();

    /// Check if this player can see an element.
    boolean canSee(Physical element);

    /// Whether this player has been defeated.
    boolean defeated();

    /// Defeat this player.
    Player defeat();

    /// This player's available supplies. Supplies are the main resource used to
    /// deploy units and construct structures.
    int supplies();

    /// This player's available supplies. Supplies are the main resource used to
    /// deploy units and construct structures.
    Player supplies(int supplies);

    /// This player's available aether. Supplies are a secondary resource used
    /// to deploy advanced units.
    int aether();

    /// This player's available aether. Supplies are a secondary resource used
    /// to deploy advanced units.
    Player aether(int aether);

    /// This player's available focus. Focus is the main resource used to cast
    /// spells.
    int focus();

    /// This player's available focus. Focus is the main resource used to cast
    /// spells.
    Player focus(int focus);

}
