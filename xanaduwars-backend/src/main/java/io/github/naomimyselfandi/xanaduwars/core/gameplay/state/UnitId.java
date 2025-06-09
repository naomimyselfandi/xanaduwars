package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a unit. Unit IDs should be regarded as somewhat sensitive
/// since they can be used to infer hidden information; for example, if a
/// player loses and regains vision of an enemy unit, if they can peek at
/// its ID, they will know that it's the same unit instead of another unit
/// of the same type.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record UnitId(@PositiveOrZero int unitId) implements AssetId, NodeId {}
