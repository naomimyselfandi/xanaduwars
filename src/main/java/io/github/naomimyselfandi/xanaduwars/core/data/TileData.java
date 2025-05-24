package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TileId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/// Low-level data about the state of a tile.
@Data
@Embeddable
@FieldNameConstants(asEnum = true)
public class TileData implements NodeData {

    /// The tile's id.
    @SuppressWarnings("com.intellij.jpb.UnsupportedTypeWithoutConverterInspection") // False positive
    private @NotNull @Valid TileId tileId;

    /// The index of the tile's original type.
    private @NotNull @PositiveOrZero Integer tileType;

    /// The index of the type of structure built on the tile, if any.
    private @Nullable @PositiveOrZero Integer structureType;

    /// The index of the tile's owner.
    private @Nullable @Valid PlayerId owner;

    /// The structure's current HP.
    private @NotNull Percent hitpoints = Percent.FULL;

    /// The structure being built on this tile, if any.
    /// @apiNote It is valid for this and [#structureType()] to be non-`null`
    /// simultaneously. This indicates that an existing structure is being
    /// upgraded.
    private @Embedded @Nullable @Valid ConstructionData construction;

    @JdbcTypeCode(SqlTypes.JSON)
    private @Unmodifiable Map<@NotNull @Valid PlayerId, @NotNull @PositiveOrZero Integer> memory = Map.of();

    /// A record of the types players think this tile is. This is used to ensure
    /// players do not become of structures being built or destroyed on tiles
    /// that they cannot see.
    public @Unmodifiable Map<@NotNull @Valid PlayerId, @NotNull @PositiveOrZero Integer> memory() {
        return Map.copyOf(memory);
    }

    /// A record of the types players think this tile is. This is used to ensure
    /// players do not become of structures being built or destroyed on tiles
    /// that they cannot see.
    public TileData memory(Map<PlayerId, Integer> memory) {
        this.memory = Map.copyOf(memory);
        return this;
    }

}
