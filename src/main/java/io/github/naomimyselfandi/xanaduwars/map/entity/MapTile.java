package io.github.naomimyselfandi.xanaduwars.map.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/// A tile in a game map. Every tile has a type, and may have a preplaced unit.
@Data
@Embeddable
public class MapTile {

    /// The name of the tile's type.
    private @NotEmpty String type;

    /// The name of the type of unit on this tile, if any.
    private @Nullable String unitType;

    /// The index of the player who owns the unit on this tile. If there is no
    /// unit on this tile, this is ignored.
    private @PositiveOrZero int unitOwner;

}
