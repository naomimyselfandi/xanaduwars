package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import jakarta.persistence.Embeddable;
import org.hibernate.validator.constraints.Range;

/// The ID of a tile.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record TileId(@Range(min = 0, max = 255) int x, @Range(min = 0, max = 255) int y)
        implements NodeId, Comparable<TileId> {

    @Override
    public int compareTo(TileId that) {
        return (this.y == that.y) ? Integer.compare(this.x, that.x) : Integer.compare(this.y, that.y);
    }

    @Override
    public String toString() {
        return "Tile(%d, %d)".formatted(x, y);
    }

}
