package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a unit in a game. Unit IDs should be regarded as sensitive, since
/// a player learning an enemy unit's ID may allow them to guess information
/// they shouldn't be able to.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record UnitId(@Column(name = "unit") @PositiveOrZero int unitId) implements NodeId, Comparable<UnitId> {

    @Override
    public int compareTo(UnitId that) {
        return Integer.compare(this.unitId, that.unitId);
    }

}
