package io.github.naomimyselfandi.xanaduwars.core.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A type of tile.
public interface TileType extends Specification {

    /// Get this tile type's unique name.
    @Override
    String getName();

    /// Get any tags that normally apply to tiles of this type.
    @Unmodifiable List<TileTag> getTags();

    /// Get the default cover provided by a tile of this type. Game rules use
    /// this in combat damage calculations at their discretion.
    double getCover();

    /// Get the movement costs various units pay to enter this tile.
    UnitSelectorMap<Double> getMovementTable();

}
