package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.TileTypeId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/// A DTO representing a tile in a game map.
@Data
public class MapTileDto {
    private @NotNull @Valid TileTypeId type;
    private @Nullable @Valid MapStructureDto structure;
    private @Nullable @Valid MapUnitDto unit;
}
