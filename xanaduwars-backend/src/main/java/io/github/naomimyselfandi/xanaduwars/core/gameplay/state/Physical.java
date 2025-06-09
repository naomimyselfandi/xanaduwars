package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import org.jetbrains.annotations.Nullable;

/// An element with a location on the game map.
public sealed interface Physical extends Element permits Asset, Node, Terrain {

    /// This element's location as a tile. This is `null` if and only if this
    /// element is a unit which is inside another unit.
    @Nullable Tile tile();

    /// Get the distance between two physical elements. The return value is
    /// always an integer unless either element is a unit inside another unit,
    /// in which case it is `NaN`.
    double distance(Physical that);

}
