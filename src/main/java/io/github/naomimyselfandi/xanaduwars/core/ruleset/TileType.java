package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.common.TileTypeId;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/// A type of tile.
public non-sealed interface TileType extends TerrainType {

    /// This tile type's ID.
    TileTypeId getId();

    /// Any tags that apply to this tile type.
    @Override @Unmodifiable Set<TileTag> getTags();

}
