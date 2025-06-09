package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.TileId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.TileTypeId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/// A low-level description of a tile.
@Data
@Embeddable
public class TileData implements Serializable {

    @Embedded
    private @NotNull @Valid TileId id;

    @Embedded
    private @NotNull @Valid TileTypeId type;

    @Embedded
    private @Nullable @Valid StructureData structureData;

    private Memory memory = Memory.NONE;

}
