package io.github.naomimyselfandi.xanaduwars.game.entity;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/// Metadata about a player in a game.
@Data
@Embeddable
public class PlayerSlot implements Serializable {

    /// The account controlling the player.
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private @NotNull Account account;

}
