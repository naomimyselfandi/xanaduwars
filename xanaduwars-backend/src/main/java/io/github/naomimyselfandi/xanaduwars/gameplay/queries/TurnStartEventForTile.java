package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;

/// An event indicating a tile's owner is starting their turn.
public record TurnStartEventForTile(@Override Tile subject) implements SubjectQuery.Event<Tile> {}
