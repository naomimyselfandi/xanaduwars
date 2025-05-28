package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.jetbrains.annotations.Nullable;

/// Low-level data about the state of a unit.
@Data
@Embeddable
@FieldNameConstants(asEnum = true)
public class UnitData implements NodeData {

    /// This unit's ID.
    @AttributeOverride(name = "intValue", column = @Column(name = "unit_id", insertable = false, updatable = false))
    private @NotNull @Valid UnitId unitId;

    /// The index of the unit's type.
    private @NotNull UnitTypeId unitType;

    /// The index of the unit's owner.
    @Embedded
    @AttributeOverride(name = "intValue", column = @Column(name = "owner"))
    private @Nullable @Valid PlayerId owner;

    /// The unit's current hit points.
    @Embedded
    @AttributeOverride(name = "doubleValue", column = @Column(name = "hitpoints"))
    private @NotNull Percent hitpoints = Percent.FULL;

    /// The ID of the unit or tile the unit is in or on.
    private @NotNull @Valid NodeId location;

    /// Whether the unit can receive orders.
    private boolean canAct;

}
