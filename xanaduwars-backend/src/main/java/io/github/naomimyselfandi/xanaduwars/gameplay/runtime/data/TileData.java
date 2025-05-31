package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.PlayerId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TileId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TileTypeId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.stream.Collectors;

/// Low-level data about the state of a tile.
@Data
@Embeddable
@FieldNameConstants(asEnum = true)
public class TileData implements NodeData {

    /// The tile's id.
    @Embedded
    private @NotNull @Valid TileId tileId;

    /// The index of the tile's original type.
    @Embedded
    private @NotNull TileTypeId tileType;

    /// The index of the type of structure built on the tile, if any.
    @Embedded
    @AttributeOverride(name = "index", column = @Column(name = "structure_type"))
    private @Nullable TileTypeId structureType;

    /// The index of the tile's owner.
    @Embedded
    @AttributeOverride(name = "intValue", column = @Column(name = "owner"))
    private @Nullable @Valid PlayerId owner;

    /// The structure's current HP.
    @Embedded
    @AttributeOverride(name = "doubleValue", column = @Column(name = "hitpoints"))
    private @NotNull Percent hitpoints = Percent.FULL;

    /// The structure being built on this tile, if any.
    /// @apiNote It is valid for this and [#structureType()] to be non-`null`
    /// simultaneously. This indicates that an existing structure is being
    /// upgraded.
    @Embedded
    private @Nullable @Valid ConstructionData construction;

    @JdbcTypeCode(SqlTypes.JSON)
    private @Unmodifiable Map<@NotNull @PositiveOrZero Integer, @NotNull @PositiveOrZero Integer> memory = Map.of();

    /// A record of the types players think this tile is. This is used to ensure
    /// players do not become of structures being built or destroyed on tiles
    /// that they cannot see.
    public @Unmodifiable Map<@NotNull @Valid PlayerId, @NotNull @Valid TileTypeId> memory() {
        return memory.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                e -> new PlayerId(e.getKey()),
                e -> new TileTypeId(e.getValue())
        ));
    }

    /// A record of the types players think this tile is. This is used to ensure
    /// players do not become of structures being built or destroyed on tiles
    /// that they cannot see.
    public TileData memory(Map<PlayerId, TileTypeId> memory) {
        this.memory = memory.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                e -> e.getKey().intValue(),
                e -> e.getValue().index()
        ));
        return this;
    }

}
