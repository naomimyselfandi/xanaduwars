package io.github.naomimyselfandi.xanaduwars.core.common;

import com.fasterxml.jackson.annotation.JsonValue;

/// A kind of element.
public enum Kind implements TargetFilter {

    PLAYER, STRUCTURE, TILE, UNIT;

    @Override
    @JsonValue
    public String toString() {
        return switch (this) {
            case PLAYER -> "Player";
            case STRUCTURE -> "Structure";
            case TILE -> "Tile";
            case UNIT -> "Unit";
        };
    }

}
