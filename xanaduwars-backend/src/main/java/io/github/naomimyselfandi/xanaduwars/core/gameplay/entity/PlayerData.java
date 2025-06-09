package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Team;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.io.Serializable;

/// A low-level description of a player.
@Data
@Embeddable
public class PlayerData implements Serializable {

    /// The player's position in the turn order.
    @Embedded
    private @NotNull @Valid PlayerId id;

    /// The player's team.
    @Embedded
    private @NotNull @Valid Team team;

    /// The ID of the commander the player is playing as.
    @Embedded
    private @NotNull @Valid CommanderId commander;

    /// A description of the player's spell slots.
    private @NotNull @Valid SpellSlotList spellSlots = SpellSlotList.NONE;

    /// The player's current supplies.
    private @PositiveOrZero int supplies;

    /// The player's current aether.
    private @PositiveOrZero int aether;

    /// The player's current focus.
    private @PositiveOrZero int focus;

    /// Whether the player has been defeated.
    private boolean defeated;

}
