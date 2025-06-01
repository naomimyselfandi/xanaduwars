package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.TileTypeId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;

import java.io.Serializable;

/// Low-level data about a structure being built on a tile.
/// @param structureType The ID of the structure type being built on the tile.
/// @param progress How much progress has been made towards this construction.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record ConstructionData(
        @Embedded
        @AttributeOverride(name = "index", column = @Column(name = "construction_structure_type"))
        @NotNull TileTypeId structureType,
        @Embedded
        @AttributeOverride(name = "doubleValue", column = @Column(name = "construction_progress"))
        @NotNull Percent progress
) implements Serializable {

}
