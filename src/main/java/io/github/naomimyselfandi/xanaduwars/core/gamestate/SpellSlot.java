package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;

import java.util.Optional;

/// A spell known by a player.
public interface SpellSlot {

    /// Get the spell in this slot.
    Optional<Spell> getSpell();

    /// Check whether the spell in this slot is revealed to enemy players.
    boolean isRevealed();

    /// Reveal the spell in this slot to enemy players.
    SpellSlot reveal();

    /// Check whether the spell in this slot is currently active.
    boolean isActive();

    /// Set whether the spell in this slot is currently active.
    SpellSlot setActive(boolean active);

}
