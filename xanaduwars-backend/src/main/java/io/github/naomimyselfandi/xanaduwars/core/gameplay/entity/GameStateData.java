package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Turn;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.NodeId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.TileId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.UnitId;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// A low-level description of a game state.
@Entity
@Getter
@Setter
@FieldNameConstants(asEnum = true)
@ToString(onlyExplicitlyIncluded = true)
public class GameStateData {

    /// Primary key.
    @Id
    @GeneratedValue
    @ToString.Include
    private @Nullable GameStateId id;

    /// The version used by the game.
    @Embedded
    @ToString.Include
    private Version version;

    /// The current turn number.
    private Turn turn;

    /// Low-level descriptions of the players in turn order.
    @OrderBy("player_id ASC")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_data", joinColumns = @JoinColumn(name = "game_state_id"))
    private @NotEmpty List<@Valid @NotNull PlayerData> playerData = new ArrayList<>();

    /// Low-level descriptions of the tiles in row-column order.
    @OrderBy("y ASC, x ASC")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tile_data", joinColumns = @JoinColumn(name = "game_state_id"))
    private @NotEmpty List<@Valid @NotNull TileData> tileData = new ArrayList<>();

    /// Low-level descriptions of the units in creation order.
    @OrderBy("unit_id ASC")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "unit_data", joinColumns = @JoinColumn(name = "game_state_id"))
    private @NotNull List<@Valid @NotNull UnitData> unitData = new ArrayList<>();

    /// The next unit ID to use.
    @Embedded
    @AttributeOverride(name = "unitId", column = @Column(name = "next_unit_id"))
    private UnitId nextUnitId = new UnitId(0);

    /// Whether the active player's turn should end. This is deferred until the
    /// end of action processing to avoid edge cases.
    private boolean pass;

    /// The width of the playable area.
    public int width() {
        return tileData.getLast().getId().x() + 1;
    }

    /// The height of the playable area.
    public int height() {
        return tileData.getLast().getId().y() + 1;
    }

    /// Find the index of the tile with some ID.
    public Optional<Integer> tileDataIndex(TileId id) {
        var x = id.x();
        var y = id.y();
        var width = width();
        var height = height();
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return Optional.of(y * width + x);
        } else {
            return Optional.empty();
        }
    }

    /// Find a tile's description by ID.
    public Optional<TileData> tileDataAt(TileId id) {
        return tileDataIndex(id).map(tileData::get);
    }

    /// Find a unit's description by ID.
    public Optional<UnitData> unitDataOf(UnitId id) {
        return unitData.stream().filter(it -> it.getId().equals(id)).findFirst();
    }

    /// Find a unit's description by location.
    public Optional<UnitData> unitDataAt(NodeId id) {
        return unitData.stream().filter(it -> it.getLocation().equals(id)).findFirst();
    }

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) return true;
        return id != null && that instanceof GameStateData d && id.equals(d.id);
    }

    @Override
    public int hashCode() {
        return GameStateData.class.hashCode();
    }

}
