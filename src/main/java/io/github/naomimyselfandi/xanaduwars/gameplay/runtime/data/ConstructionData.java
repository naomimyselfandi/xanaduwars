package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.TileTypeId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;

import java.io.Serializable;

/// Low-level data about a structure being built on a tile.
@Data
@Embeddable
@FieldNameConstants(asEnum = true)
public class ConstructionData implements Serializable {

    /// The index of the structure type being built on the tile.
    @Embedded
    @AttributeOverride(name = "index", column = @Column(name = "construction_structure_type"))
    private @NotNull TileTypeId structureType;

    /// How much progress has been made towards building the structure.
    @Embedded
    @AttributeOverride(name = "doubleValue", column = @Column(name = "construction_progress"))
    private @NotNull Percent progress;

}
