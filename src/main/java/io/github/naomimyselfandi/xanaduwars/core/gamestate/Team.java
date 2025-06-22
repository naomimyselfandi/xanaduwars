package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;

/// The ID of a team in a game.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Team(@Column @JsonValue @PositiveOrZero int team) implements Serializable {

    @Override
    public String toString() {
        return String.valueOf(team);
    }

}
