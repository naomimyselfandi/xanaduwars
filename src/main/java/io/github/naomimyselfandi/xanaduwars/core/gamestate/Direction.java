package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.annotation.JsonProperty;

/// A compass direction.
public enum Direction {

    /// One step in the negative Y-axis.
    @JsonProperty("N") NORTH,

    /// One step in the positive X-axis.
    @JsonProperty("E") EAST,

    /// One step in the positive Y-axis.
    @JsonProperty("S") SOUTH,

    /// One step in the negative X-axis.
    @JsonProperty("W") WEST,

}
