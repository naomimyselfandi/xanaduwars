package io.github.naomimyselfandi.xanaduwars.core.wrapper;

/// The ID of a unit or tile.
public sealed interface NodeId extends IntWrapper permits TileId, UnitId {

    /// Unpack a node ID from its integer representation.
    static NodeId withIntValue(int intValue) {
        if (intValue < 0) {
            return TileId.withIntValue(intValue);
        } else {
            return new UnitId(intValue);
        }
    }

}
