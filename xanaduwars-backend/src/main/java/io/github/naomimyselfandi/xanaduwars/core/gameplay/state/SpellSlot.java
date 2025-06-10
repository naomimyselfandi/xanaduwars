package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Spell;

/// A player's spell slot.
public interface SpellSlot {

    /// The spell in this slot.
    Spell getSpell();

    /// Whether the spell in this slot is revealed. A spell slot starts off
    /// revealed if it holds the player's commander's signature spell; other
    /// slots become revealed as the spells in them are cast.
    boolean isRevealed();

    /// How many times the player has cast the spell in this slot this turn.
    int getCastsThisTurn();

}
