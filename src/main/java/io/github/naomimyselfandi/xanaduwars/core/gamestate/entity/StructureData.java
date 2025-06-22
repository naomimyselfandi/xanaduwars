package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Hp;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/// A low-level description of a structure.
@Data
@Embeddable
public class StructureData implements Serializable {

    /// The ID of the structure's type.
    private @NotNull @Valid StructureTypeId typeId;

    /// The structure's current HP.
    private @NotNull @Valid Hp hp = Hp.FULL;

    /// Whether the structure is incomplete. Incomplete structures cannot take
    /// actions and do not influence terrain.
    private boolean incomplete;

    /// The ID of the player who owns this structure.
    private @Nullable @Valid PlayerId playerId;

}
