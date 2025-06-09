package io.github.naomimyselfandi.xanaduwars.core.gameplay.dto;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.TileId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.TileTypeId;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/// A DTO used to represent a tile.
@Data
public class TileDto {
    private TileId id;
    private TileTypeId type;
    private @Nullable StructureDto structure;
    private @Nullable UnitDto unit;
}
