package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Affinity;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Tag;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

/// A playable commander. Commanders act as the "type" of a player.
public non-sealed interface Commander extends Type {

    /// This commander's index. This is zero for the first commander, one for
    /// the second commander, and so on.
    @Override
    CommanderId id();

    /// This commander's signature spells. A player playing as this commander
    /// always has access to these spells.
    @Unmodifiable List<SpellType<?>> signatureSpells();

    /// This commander's affinities for various spells.
    @Unmodifiable Map<Tag, Affinity> affinities();

}
