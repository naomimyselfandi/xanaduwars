package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Team;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/// A low-level description of a player.
@Data
@Embeddable
public class PlayerData implements Serializable {

    /// The ID of the commander the player is playing as. If `null`, the player
    /// has not chosen a commander.
    @Embedded
    private @Nullable @Valid CommanderId commanderId;

    /// The team the player is on.
    @Embedded
    private @NotNull @Valid Team team;

    /// The player's current supplies. Supplies are the main resource used to
    /// deploy units.
    private @PositiveOrZero int supplies;

    /// The player's current aether. Aether is a secondary resource used to
    /// deploy advanced units.
    private @PositiveOrZero int aether;

    /// The player's current focus. Focus is the main resource used to cast
    /// spells.
    private @PositiveOrZero int focus;

    /// A bitset tracking which of the player's signature spells are active.
    @Embedded
    @AttributeOverride(name = "bits", column = @Column(name = "signature_spell_activation"))
    private Bitset signatureSpellActivation = new Bitset();

    /// A bitset tracking which of the player's chosen spells are active.
    @Embedded
    @AttributeOverride(name = "bits", column = @Column(name = "chosen_spell_activation"))
    private Bitset chosenSpellActivation = new Bitset();

    /// A bitset tracking which of the player's chosen spells are revealed.
    @Embedded
    @AttributeOverride(name = "bits", column = @Column(name = "chosen_spell_revelation"))
    private Bitset chosenSpellRevelation = new Bitset();

    @Embedded
    private ChosenSpells chosenSpells = new ChosenSpells();

    /// Whether the player has been defeated.
    private boolean defeated;

}
