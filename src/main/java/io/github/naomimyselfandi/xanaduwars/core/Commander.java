package io.github.naomimyselfandi.xanaduwars.core;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.Tag;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

/// A playable commander. Commanders act as the "type" of a player.
public non-sealed interface Commander extends Type {

    /// This commander's signature spells. A player playing as this commander
    /// always has access to these spells.
    @Unmodifiable List<SpellType<?>> signatureSpells();

    /// This commander's affinities for various spells.
    @Unmodifiable Map<Tag, Affinity> affinities();

}
