package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

/// A playable commander.
public non-sealed interface Commander extends ActorType, Rule {

    /// This commander's ID.
    CommanderId id();

    /// This commander's signature spells.
    @Unmodifiable List<Spell> signatureSpells();

    /// This commander's affinities for various spells.
    @Unmodifiable Map<SpellTag, Affinity> affinities();

}
