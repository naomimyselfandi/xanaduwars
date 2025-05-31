package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

/// The ID of a spell type.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record SpellTypeId(@JsonValue @Column(name = "spell_type") @PositiveOrZero int index) implements TypeId {

    @Override
    public String toString() {
        return "SpellType[%d]".formatted(index);
    }

}
