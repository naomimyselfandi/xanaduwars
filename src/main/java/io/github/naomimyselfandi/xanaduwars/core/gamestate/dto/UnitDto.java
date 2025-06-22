package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Hp;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.TileId;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/// A DTO representing a unit.
@Data
public class UnitDto {
    private UnitTypeId type;
    private Set<UnitTag> tags;
    private Hp hp;
    private PlayerId owner;
    private int speed, vision;
    private @Nullable UnitDto cargo;
    private boolean ready;
    private @Nullable TileId tile; // Convenience denormalization
}
