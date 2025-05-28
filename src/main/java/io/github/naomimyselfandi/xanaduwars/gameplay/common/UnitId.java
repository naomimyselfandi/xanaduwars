package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a unit.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record UnitId(@Column(name = "unit_id") @PositiveOrZero @JsonValue int intValue)
        implements NodeId, Comparable<UnitId> {

    @Override
    public int compareTo(UnitId that) {
        return Integer.compare(this.intValue, that.intValue);
    }

    @Override
    public String toString() {
        return "Unit(%d)".formatted(intValue);
    }

}
