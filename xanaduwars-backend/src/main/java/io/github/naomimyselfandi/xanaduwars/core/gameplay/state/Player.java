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
    PlayerId getId();

    /// This player's team. In a one-on-one or free-for-all game, each player is
    /// simply assigned a unique team, typically derived from their player ID.
    Team getTeam();

    /// The commander this player is playing as.
    Commander getCommander();

    /// This player's spell slots.
    @Unmodifiable List<SpellSlot> getSpellSlots();

    /// This player's units in creation order.
    Stream<Unit> getUnits();

    /// This player's structures in tile order.
    Stream<Structure> getStructures();

    /// Check if this player can see an element.
    boolean canSee(Physical element);

    /// Whether this player has been defeated.
    boolean isDefeated();

    /// Defeat this player.
    Player defeat();

    /// This player's available supplies. Supplies are the main resource used to
    /// deploy units and construct structures.
    int getSupplies();

    /// This player's available supplies. Supplies are the main resource used to
    /// deploy units and construct structures.
    Player setSupplies(int supplies);

    /// This player's available aether. Supplies are a secondary resource used
    /// to deploy advanced units.
    int getAether();

    /// This player's available aether. Supplies are a secondary resource used
    /// to deploy advanced units.
    Player setAether(int aether);

    /// This player's available focus. Focus is the main resource used to cast
    /// spells.
    int getFocus();

    /// This player's available focus. Focus is the main resource used to cast
    /// spells.
    Player setFocus(int focus);

}
