package io.github.naomimyselfandi.xanaduwars.map.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/// A slot for a player in a game map.
@Data
@Embeddable
public class PlayerSlot {

    /// The team the player in this slot will be assigned to.
    private @PositiveOrZero int team;

}
