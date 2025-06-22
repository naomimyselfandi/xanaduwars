package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.common.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.TileType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.stream.Stream;

/// A tile in a game.
public non-sealed interface Tile extends Node {

    /// Get this tile's ID. Tiles are identified by their coordinates.
    @Override
    TileId getId();

    /// Get this tile's type.
    TileType getType();

    /// Set this tile's type.
    Tile setType(TileType type);

    /// Get any tags that apply to this tile.
    @Unmodifiable Set<TileTag> getTags();

    /// Get the structure on this tile, if any.
    @Nullable Structure getStructure();

    /// Get the unit on this tile, if any.
    @Nullable Unit getUnit();

    /// Get the movement cost a unit would pay to enter this tile. A cost of
    /// `NaN` indicates that the unit cannot enter this tile. If there is a
    /// structure on this tile, its movement costs override this tile's normal
    /// movement costs, but other situational factors, such as the presence of a
    /// unit on this tile, are nto considered.
    double getMovementCost(Unit unit);

    /// Get the defensive cover applied by this tile.
    double getCover();

    /// Get the distance between two tiles.
    int getDistance(Tile that);

    @Nullable Tile step(Direction direction);

    /// Get all tiles in the given radius of this one, in row-column order.
    Stream<Tile> getArea(int radius);

    /// Get the structure type a player remembers being on this tile.
    @Nullable StructureType getMemory(Player player);

    /// Set the structure type a player remembers being on this tile.
    Tile setMemory(Player player, @Nullable StructureType structureType);

    @Override
    @Contract("-> null")
    @Nullable Player getOwner();

    @Override
    @Contract("-> this")
    Tile getTile();

}
