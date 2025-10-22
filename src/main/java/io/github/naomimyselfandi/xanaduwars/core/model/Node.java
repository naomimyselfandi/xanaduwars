package io.github.naomimyselfandi.xanaduwars.core.model;

import org.jetbrains.annotations.Nullable;

/// A unit or tile.
public sealed interface Node extends Element permits Tile, Unit {

    /// Get the unit in or on this node, if any.
    @Nullable Unit getUnit();

    /// Set the unit in or on this node, if any.
    Node setUnit(@Nullable Unit unit);

    /// Get the distance between two nodes. If either node is a unit which is
    /// inside another unit, this is [NaN][Double#NaN]; in all other cases, it
    /// is a non-negative integer.
    double getDistance(Node other);

    /// Create a unit in or on this node.
    Unit createUnit(UnitType type, Player owner);

    /// Play an animation on this node.
    void play(Animation animation);

}
