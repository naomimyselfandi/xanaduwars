package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/// A type of tile.
public non-sealed interface TileType extends NodeType, TerrainType {

    /// This tile type's ID.
    TileTypeId id();

    /// Any tags that apply to this tile type.
    @Override @Unmodifiable Set<TileTag> tags();

}
