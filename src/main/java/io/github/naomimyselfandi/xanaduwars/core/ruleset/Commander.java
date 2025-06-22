package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellTag;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

/// A playable commander.
public non-sealed interface Commander extends Declaration, Rule {

    /// This commander's ID.
    CommanderId getId();

    /// This commander's chosen spell count. During pregame spell selection, a
    /// player must choose exactly this many spells. Their commander's signature
    /// spells do not count towards this limit.
    int getChosenSpells();

    /// This commander's signature spells. During pregame spell selection, a
    /// player automatically gains access to their commander's signature spells,
    /// and may not choose another commander's signature spells.
    @Unmodifiable List<Spell> getSignatureSpells();

    /// This commander's affinities for various spells. This has no inherent
    /// effect, but game rules may read affinities and interact with them.
    @Unmodifiable Map<SpellTag, Affinity> getAffinities();

}
