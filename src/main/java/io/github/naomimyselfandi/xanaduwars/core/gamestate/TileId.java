package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.validator.constraints.Range;

/// The ID of a tile in a game. Tiles are identified by their coordinates.
/// @param x The X-coordinate of this tile ID.
/// @param y The Y-coordinate of this tile ID.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record TileId(
        @Column @Range(min = 0, max = 999) int x,
        @Column @Range(min = 0, max = 999) int y
) implements NodeId, Comparable<TileId> {

    /// The structure ID corresponding to this tile ID.
    @JsonIgnore
    public StructureId structureId() {
        return new StructureId(x, y);
    }

    @Override
    public int compareTo(TileId that) {
        return (this.y == that.y) ? Integer.compare(this.x, that.x) : Integer.compare(this.y, that.y);
    }

}
