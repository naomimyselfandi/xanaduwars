package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.NodeId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.UnitId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import org.jetbrains.annotations.Nullable;

/// Low-level data about the state of a unit.
@Data
@Embeddable
@FieldNameConstants(asEnum = true)
public class UnitData implements NodeData {

    /// This unit's ID.
    @Column(name = "unit_id", insertable = false, updatable = false)
    @SuppressWarnings("com.intellij.jpb.UnsupportedTypeWithoutConverterInspection") // False positive
    private @NotNull @Valid UnitId unitId;

    /// The index of the unit's type.
    private @NotNull @PositiveOrZero Integer unitType;

    /// The index of the unit's owner.
    private @Nullable @Valid PlayerId owner;

    /// The unit's current hit points.
    private @NotNull Percent hitpoints = Percent.FULL;

    /// The ID of the unit or tile the unit is in or on.
    private @NotNull @Valid NodeId location;

    /// Whether the unit can receive orders.
    private boolean canAct;

}
