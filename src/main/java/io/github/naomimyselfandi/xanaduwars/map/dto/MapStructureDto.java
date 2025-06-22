package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Hp;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/// A DTO representing a structure in a game map.
@Data
public class MapStructureDto {
    private @NotNull @Valid StructureTypeId type;
    private @Nullable @Valid PlayerId owner;
    private @NotNull @Valid Hp hp = Hp.FULL;
}
