package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.NodeId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.UnitId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitTypeId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.Nullable;

/// A low-level description of a unit.
@Data
@Embeddable
public class UnitData implements AssetData {

    /// The unit's unique ID.
    @Embedded
    private @NotNull @Valid UnitId id;

    /// The ID of the unit's type.
    @Embedded
    private @NotNull @Valid UnitTypeId type;

    /// The ID of the unit's owner.
    @Embedded
    private @Nullable @Valid PlayerId owner;

    /// The unit's HP.
    private @NotNull @Range(min = 0, max = Asset.FULL_HP) int hp = Asset.FULL_HP;

    /// The ID of the unit's location.
    private @NotNull @Valid NodeId location;

    /// The names of the actions the unit has taken this turn.
    private History history = History.NONE;

}
