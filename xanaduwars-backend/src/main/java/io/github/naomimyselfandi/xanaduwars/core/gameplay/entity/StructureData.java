package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.Nullable;

/// A low-level description of a structure.
@Data
@Embeddable
public class StructureData implements AssetData {

    /// The ID of the structure's type.
    @Embedded
    private @NotNull @Valid StructureTypeId type;

    /// The player who owns the structure.
    @Embedded
    private @Nullable @Valid PlayerId owner;

    /// The structure data's current HP.
    private @NotNull @Range(min = 0, max = Asset.FULL_HP) int hp;

    /// Whether the structure is complete.
    private boolean complete;

}
