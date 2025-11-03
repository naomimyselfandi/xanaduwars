package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.xanaduwars.util.NotCovered;

/// A direction on the game map.
@NotCovered // trivial
public enum Direction {

    /// Movement along the negative Y-axis.
    NORTH,

    /// Movement along the positive X-axis.
    EAST,

    /// Movement along the positive Y-axis.
    SOUTH,

    /// Movement along the negative X-axis.
    WEST,

}
