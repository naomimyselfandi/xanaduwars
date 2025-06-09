package io.github.naomimyselfandi.xanaduwars.core.gameplay.dto;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.TileId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/// A DTO used to represent a structure.
@Data
public class StructureDto {
    private TileId id; // Denormalization for the client's convenience
    private StructureTypeId type;
    private @Nullable PlayerId owner;
    private int hp;
    private boolean complete;
}
