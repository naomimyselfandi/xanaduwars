package io.github.naomimyselfandi.xanaduwars.map.entity;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/// A game map. A map is used to create a game state, and describes the tiles,
/// preplaced units, and player slots the game will start with.
@Entity
@Getter
@Setter
public class GameMap extends AbstractEntity<GameMap> {

    /// A game map's status.
    @NotCovered // Trivial
    public enum Status {

        /// The map's author has not published it, and no one else can use it.
        UNPUBLISHED,

        /// The map's author has published it, so anyone can use it.
        PUBLISHED,

        /// The map has been submitted for tournament use.
        SUBMITTED,

        /// The map has been approved for tournament use.
        OFFICIAL,

    }

    /// This map's unique name.
    @Pattern(regexp = "^\\S+(\\s\\S+)*$")
    @Pattern(regexp = "^[\\p{L}\\p{N} _\\-!@#$%^+=.]{1,32}$")
    private @NotNull String name;

    /// The map's current status.
    @Enumerated(EnumType.STRING)
    private Status status;

    /// The account that created this map.
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Account author;

    /// The map's width.
    private int width;

    /// The map's height.
    private int height;

    /// This map's tiles.
    @ElementCollection
    @OrderColumn(name = "index")
    @CollectionTable(name = "map_tile", joinColumns = @JoinColumn(name = "map_id"))
    private @NotEmpty List<MapTile> tiles = new ArrayList<>();

    /// This map's player slots.
    @ElementCollection
    @OrderColumn(name = "index")
    @CollectionTable(name = "player_slot", joinColumns = @JoinColumn(name = "map_id"))
    private @NotEmpty List<PlayerSlot> playerSlots = new ArrayList<>();

    @AssertTrue
    boolean isRectangle() {
        return tiles.size() == (width * height);
    }

}
