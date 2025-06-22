package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.common.TileTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.TileId;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/// A DTO representing a tile.
@Data
public class TileDto {
    private TileId id;
    private TileTypeId type;
    private Set<TileTag> tags;
    private @Nullable UnitDto unit;
    private @Nullable StructureDto structure;
    private double cover;
}
