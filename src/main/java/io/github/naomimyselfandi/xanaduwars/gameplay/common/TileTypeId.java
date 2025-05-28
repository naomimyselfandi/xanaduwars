package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a tile type.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record TileTypeId(@JsonValue @Column(name = "tile_type") @PositiveOrZero int index) implements NodeTypeId {

    @Override
    public String toString() {
        return "TileType[%d]".formatted(index);
    }

}
