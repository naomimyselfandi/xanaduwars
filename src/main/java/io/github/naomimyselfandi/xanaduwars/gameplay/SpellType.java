package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.SpellTypeId;

/// A type of spell.
public non-sealed interface SpellType<T> extends Type, Action<Player, T> {

    /// This spell type's index. This is zero for the first spell type, one for
    /// the second spell type, and so on.
    @Override
    SpellTypeId id();


}
