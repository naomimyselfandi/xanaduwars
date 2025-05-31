package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;

/// An event indicating a tile was destroyed.
public record TileDestroyedEvent(@Override Tile subject) implements NodeDestroyedEvent<Tile> {}
