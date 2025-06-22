package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Hp;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.StructureId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.TileId;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/// A DTO representing a structure.
@Data
public class StructureDto {
    private StructureId id;
    private StructureTypeId type;
    private Set<StructureTag> tags;
    private Hp hp;
    private PlayerId owner;
    private int vision;
    private boolean incomplete;
    private @Nullable TileId tile; // Convenience denormalization
}
