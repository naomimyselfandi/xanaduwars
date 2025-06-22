package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellTag;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/// A spell a player can cast.
public non-sealed interface Spell extends Action, Declaration, Rule {

    /// This spell's ID.
    SpellId getId();

    /// Any tags that apply to this spell.
    @Override @Unmodifiable Set<SpellTag> getTags();

    /// Whether this is a commander's signature spell.
    boolean isSignatureSpell();

    /// The focus cost to cast this spell.
    @Override
    FixedCost getFocusCost();

}
