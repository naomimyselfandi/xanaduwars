package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.UnitTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Hp;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/// A DTO representing a unit in a game map.
@Data
public class MapUnitDto {
    private @NotNull @Valid UnitTypeId type;
    private @Nullable @Valid PlayerId owner;
    private @NotNull @Valid Hp hp = Hp.FULL;
}
