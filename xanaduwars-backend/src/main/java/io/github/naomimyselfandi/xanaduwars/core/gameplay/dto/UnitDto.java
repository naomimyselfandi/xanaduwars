package io.github.naomimyselfandi.xanaduwars.core.gameplay.dto;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.TileId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitTypeId;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/// A DTO used to represent a unit.
@Data
public class UnitDto {
    private UnitTypeId type;
    private PlayerId owner;
    private @Nullable TileId location; // Denormalization for the client's convenience
    private int hp;
    private boolean moved, acted;
}
