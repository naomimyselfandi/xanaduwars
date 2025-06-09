package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

/// A game state element that can contain units.
public sealed interface Node extends Physical permits Tile, Unit {}
