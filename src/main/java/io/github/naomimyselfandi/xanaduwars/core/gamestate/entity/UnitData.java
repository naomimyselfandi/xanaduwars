package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.common.UnitTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Hp;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.NodeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/// A low-level description of a unit.
@Data
@Embeddable
public class UnitData implements Serializable {

    /// The ID of the unit's type.
    private @NotNull @Valid UnitTypeId typeId;

    /// The unit's current HP.
    private @NotNull @Valid Hp hp = Hp.FULL;

    /// Whether the unit is ready to take actions.
    private boolean ready;

    /// The ID of the player who owns this unit.
    private @Nullable @Valid PlayerId playerId;

    /// The ID of the unit's location.
    @Column(name = "location")
    private @NotNull @Valid NodeId locationId;

}
