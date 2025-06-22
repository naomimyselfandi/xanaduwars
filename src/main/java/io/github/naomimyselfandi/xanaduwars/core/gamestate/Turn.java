package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

import java.io.Serializable;

/// A turn number.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Turn(@Column(name = "turn") @JsonValue @With @PositiveOrZero int ordinal)
        implements Serializable, Ordinal<Turn> {

    @Override
    public String toString() {
        return String.valueOf(ordinal);
    }

}
