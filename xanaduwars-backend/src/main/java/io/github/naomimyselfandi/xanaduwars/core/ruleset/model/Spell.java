package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/// A spell a player can cast.
public non-sealed interface Spell extends Declaration, Action, Rule {

    /// This spell's ID.
    SpellId getId();

    /// Any tags that apply to this spell.
    @Override @Unmodifiable Set<SpellTag> getTags();

    /// The focus cost to cast this spell.
    int getFocusCost();

}
