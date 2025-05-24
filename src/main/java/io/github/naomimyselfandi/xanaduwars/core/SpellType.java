package io.github.naomimyselfandi.xanaduwars.core;

/// A type of spell.
public non-sealed interface SpellType<T> extends Type, Action<Player, T> {}
