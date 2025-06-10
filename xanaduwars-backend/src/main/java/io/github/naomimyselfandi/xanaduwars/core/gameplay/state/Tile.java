package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Stream;

/// A tile on the game map.
public non-sealed interface Tile extends Node, Terrain {

    /// This tile's ID.
    TileId getId();

    /// The type of tile this is.
    TileType getType();

    /// Any tags that apply to this tile.
    Set<TileTag> getTags();

    /// The structure on this tile, if any.
    @Nullable Structure getStructure();

    /// The unit on this tile, if any.
    @Nullable Unit getUnit();

    /// Look up the movement cost for a unit to enter this tile. If the unit
    /// cannot enter this tile, `NaN` is returned.
    double getMovementCost(Unit unit);

    /// Create a unit on this tile.
    /// @throws IllegalStateException if this tile already has a unit.
    void createUnit(UnitType type, Player owner);

    /// Create a structure on this tile.
    /// @throws IllegalStateException if this tile already has a structure.
    void createStructure(StructureType type, Player owner);

    /// Get the distance between two tiles.
    int getDistance(Tile that);

    /// Get the tile one step in the given direction from this one.
    @Nullable Tile step(Direction direction);

    /// Get the tiles within the given radius of this one. The tiles are
    /// returned in row-column order.
    Stream<Tile> area(int radius);

    @Override
    @Contract("-> this")
    Tile getTile();

}
