package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.validation.constraints.Negative;
import org.hibernate.validator.constraints.Range;

/// The ID of a tile. Tile IDs can be packed as single integers. A tile ID's
/// integer representation is `-100Y00X` where `00X` and `00Y` denote the X-
/// and Y-coordinates padded to three digits.
public record TileId(@Range(min = 0, max = 999) int x, @Range(min = 0, max = 999) int y)
        implements NodeId, Comparable<TileId> {

    private static final int SCALE = 1000;
    private static final int OFFSET = SCALE * SCALE;

    /// Unpack a tile ID from its integer representation.
    public static TileId withIntValue(int intValue) {
        var i = -intValue - OFFSET;
        var x = (i % SCALE);
        var y = (i / SCALE);
        return new TileId(x, y);
    }

    @Override
    @Negative
    public int intValue() {
        return -((SCALE * y) + x + OFFSET);
    }

    @Override
    public int compareTo(TileId that) {
        return (this.y == that.y) ? Integer.compare(this.x, that.x) : Integer.compare(this.y, that.y);
    }

    @Override
    public String toString() {
        return "Tile(%d, %d)".formatted(x, y);
    }

}
