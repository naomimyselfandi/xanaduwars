package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Range;

/// The ID of a structure in a game. A structure is identified by the
/// coordinates of its tile.
/// @param x The X-coordinate of this tile ID.
/// @param y The Y-coordinate of this tile ID.
@Embeddable
@FieldNameConstants
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record StructureId(
        @Column @Range(min = 0, max = 999) int x,
        @Column @Range(min = 0, max = 999) int y
) implements ElementId, Comparable<StructureId> {

    /// The tile ID corresponding to this structure ID.
    @JsonIgnore
    public TileId tileId() {
        return new TileId(x, y);
    }

    @Override
    public int compareTo(StructureId that) {
        return (this.y == that.y) ? Integer.compare(this.x, that.x) : Integer.compare(this.y, that.y);
    }

}
