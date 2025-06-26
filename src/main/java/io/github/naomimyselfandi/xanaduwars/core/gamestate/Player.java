package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/// A player in a game.
public non-sealed interface Player extends Element {

    /// This player's ID. This is also their position in the turn order: the
    /// starting player has an ID of zero, the next player has an ID of one,
    /// and so on.
    @Override
    PlayerId getId();

    /// Get the commander this player is playing as.
    Optional<Commander> getCommander();

    /// Set the commander this player is playing as.
    Player setCommander(Commander commander);

    /// Get the team this player is on. In a non-team game, this is an arbitrary
    /// unique value, effectively placing each player on a team of one.
    Team getTeam();

    /// Check if this player can see a physical game element.
    boolean canSee(Physical element);

    /// Get this player's spell slots.
    @Unmodifiable List<SpellSlot> getSpellSlots();

    /// Get this player's chosen spells.
    @Unmodifiable List<Spell> getChosenSpells();

    /// Set this player's chosen spells.
    Player setChosenSpells(List<Spell> chosenSpells);

    /// Get this player's current supplies. Supplies are the main resource used
    /// to deploy units.
    int getSupplies();

    /// Set this player's current supplies. Supplies are the main resource used
    /// to deploy units.
    Player setSupplies(int supplies);

    /// Get this player's current aether. Aether is a secondary resource used to
    /// deploy advanced units.
    int getAether();

    /// Set this player's current aether. Aether is a secondary resource used to
    /// deploy advanced units.
    Player setAether(int aether);

    /// Get this player's current focus. Focus is the main resource used to cast
    /// spells.
    int getFocus();

    /// Set this player's current focus. Focus is the main resource used to cast
    /// spells.
    Player setFocus(int focus);

    /// Check if this player has been defeated.
    boolean isDefeated();

    /// Defeat this player.
    Player defeat();

    /// Get this player's structures in tile order.
    Stream<Structure> getStructures();

    /// Get this player's units in creation order.
    Stream<Unit> getUnits();

    @Override
    Optional<Player> getOwner();

}
