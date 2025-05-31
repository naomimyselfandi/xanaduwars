package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a unit type.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record UnitTypeId(@JsonValue @Column(name = "unit_type") @PositiveOrZero int index) implements NodeTypeId {

    @Override
    public String toString() {
        return "UnitType[%d]".formatted(index);
    }

}
