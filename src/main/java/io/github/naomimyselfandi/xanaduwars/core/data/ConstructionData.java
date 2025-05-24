package io.github.naomimyselfandi.xanaduwars.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;

import java.io.Serializable;

/// Low-level data about a structure being built on a tile.
@Data
@Embeddable
@FieldNameConstants(asEnum = true)
public class ConstructionData implements Serializable {

    /// The index of the structure type being built on the tile.
    @Column(name = "construction_structure_type")
    private @PositiveOrZero @NotNull Integer structureType;

    /// How much progress has been made towards building the structure.
    @Column(name = "construction_progress")
    private @NotNull Percent progress;

}
