package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a playable commander.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record CommanderId(@JsonValue @Column(name = "commander") @PositiveOrZero int index) implements TypeId {

    @Override
    public String toString() {
        return "Commander[%d]".formatted(index);
    }

}
