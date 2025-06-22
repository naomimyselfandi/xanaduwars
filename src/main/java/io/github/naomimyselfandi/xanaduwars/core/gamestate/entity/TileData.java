package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.common.TileTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/// A low-level description of a tile.
@Data
@Embeddable
public class TileData implements Serializable {

    /// The ID of the tile's type.
    private TileTypeId typeId;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JdbcTypeCode(SqlTypes.JSON)
    private @Nullable @Valid Memory memory;

    /// Get the ID of the structure type the given player remembers being here.
    public @Nullable StructureTypeId getMemory(PlayerId playerId) {
        return memory == null ? null : memory.getMemory().get(playerId);
    }

    /// Get the ID of the structure type the given player remembers being here.
    public TileData setMemory(PlayerId playerId, @Nullable StructureTypeId structureTypeId) {
        var map = (memory == null ? memory = new Memory() : memory).getMemory();
        if (structureTypeId == null) {
            map.remove(playerId);
        } else {
            map.put(playerId, structureTypeId);
        }
        if (map.isEmpty()) memory = null;
        return this;
    }

}
