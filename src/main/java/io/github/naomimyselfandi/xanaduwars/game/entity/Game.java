package io.github.naomimyselfandi.xanaduwars.game.entity;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
public class Game extends AbstractEntity<Game> {

    /// A game's status.
    public enum Status {PENDING, ONGOING, CANCELED, FINISHED}

    /// This game's current status.
    @Enumerated(EnumType.STRING)
    private @NotNull Status status = Status.PENDING;

    /// This game's host.
    @ManyToOne(optional = false)
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    private Account host;

    /// This game's current state.
    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "game_state_id", referencedColumnName = "id")
    private @NotNull @Embedded GameStateData gameStateData;

    /// This game's player slots.
    @ElementCollection
    @CollectionTable(name = "player_slots")
    private @NotEmpty SortedMap<PlayerId, PlayerSlot> playerSlots = new TreeMap<>();

    /// This game's replay.
    @ElementCollection
    @OrderColumn(name = "seq")
    @CollectionTable(name = "replay")
    private @NotEmpty List<ReplayEntry> replay = new ArrayList<>();

    /// Check if this game has started.
    public boolean hasStarted() {
        return switch (status) {
            case PENDING, CANCELED -> false;
            case ONGOING, FINISHED -> true;
        };
    }

    @AssertTrue
    boolean hasUniquePlayers() {
        return playerSlots.values().stream().map(PlayerSlot::getAccount).distinct().count() == playerSlots.size();
    }

}
