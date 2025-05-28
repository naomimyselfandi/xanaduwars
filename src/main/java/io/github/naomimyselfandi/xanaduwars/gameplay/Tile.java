package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/// A tile. Tiles control how units move, and also provide defensive cover to
/// units on them.
public non-sealed interface Tile extends Node {

    /// The type of tile this is.
    @Override
    TileType type();

    /// The type of tile this structure was built on. If this is not present,
    /// this tile is not a structure.
    Optional<TileType> foundation();

    /// This tile's ID, which defines its coordinates.
    TileId id();

    /// The distance between this tile and another tile.
    int distance(Tile that);

    /// The unit on this tile, if any.
    Optional<Unit> unit();

    /// The tiles within the given radius of this one, in row-column order. This
    /// includes this tile itself.
    Stream<Tile> area(int radius);

    /// The tile one step in a given direction of this one.
    Optional<Tile> step(Direction direction);

    /// This tile's movement table. If a unit has a tag which is a key into the
    /// map, it can enter this tile; the corresponding value indicates how much
    /// of its speed it spends to do so.
    Map<Tag, Double> movementTable();

    /// The cover provided by this tile. This multiplies the damage received by
    /// a unit on this tile.
    Percent cover();

    /// The resources this tile provides every turn.
    Map<Resource, Integer> income();

    /// The types of unit this tile can deploy.
    Set<UnitType> deploymentRoster();

    /// The incomplete structure on this tile, if any.
    Optional<Construction> construction();

    /// The incomplete structure on this tile, if any.
    Tile construction(@Nullable Construction construction);

    /// Create a structure on this tile with the given type and owner.
    void createStructure(TileType type, Player owner);

    /// Create a unit on this tile with the given type and owner.
    /// @throws IllegalStateException if there is already a unit on this tile.
    void createUnit(UnitType type, Player owner);

}
