package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureType;

/// A structure built on top of some tile.
public non-sealed interface Structure extends Asset, Terrain {

    /// The tile this structure was built on.
    @Override
    Tile getTile();

    /// The type of structure this is.
    StructureType getType();

    /// Whether this structure was completed. An incomplete structure cannot
    /// take actions.
    boolean isComplete();

    /// Whether this structure was completed. An incomplete structure cannot
    /// take actions.
    Structure setComplete(boolean complete);

}
