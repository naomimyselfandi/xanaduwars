package io.github.naomimyselfandi.xanaduwars.core.model;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

/// A tile in a game.
public non-sealed interface Tile extends Node {

    /// Get this tile's X-coordinate.
    int getX();

    /// Get this tile's Y-coordinate.
    int getY();

    /// Get the tile one step in the given direction, if any.
    @Nullable Tile step(Direction direction);

    /// Get this tile's type.
    TileType getType();

    /// Set this tile's type.
    Tile setType(TileType type);

    /// Get any tags that apply to this tile.
    List<TileTag> getTags();

    /// Get the unit on this tile, if any.
    @Nullable Unit getUnit();

    /// Get the cover provided by this tile. Game rules use this in combat
    /// damage calculations at their discretion.
    double getCover();

    /// Get the movement costs various units pay to enter this tile.
    @Unmodifiable Map<UnitTag, Double> getMovementTable();

}
