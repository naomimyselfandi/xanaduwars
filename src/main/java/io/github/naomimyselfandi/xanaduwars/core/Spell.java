package io.github.naomimyselfandi.xanaduwars.core;

/// A spell cast by a player.
public non-sealed interface Spell extends Element {

    /// The type of spell this is.
    @Override
    SpellType<?> type();

}
