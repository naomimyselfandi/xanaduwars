package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a unit.
public record UnitId(@PositiveOrZero @Override @JsonValue int intValue) implements NodeId, Comparable<UnitId> {

    @Override
    public int compareTo(UnitId that) {
        return Integer.compare(this.intValue, that.intValue);
    }

    @Override
    public String toString() {
        return "Unit(%d)".formatted(intValue);
    }

}
