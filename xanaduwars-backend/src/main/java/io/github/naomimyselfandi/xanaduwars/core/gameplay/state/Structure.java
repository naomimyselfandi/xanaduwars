package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureType;

/// A structure built on top of some tile.
public non-sealed interface Structure extends Asset, Terrain {

    /// The tile this structure was built on.
    @Override
    Tile tile();

    /// The type of structure this is.
    StructureType type();

    /// Whether this structure was completed. An incomplete structure cannot
    /// take actions.
    boolean complete();

    /// Whether this structure was completed. An incomplete structure cannot
    /// take actions.
    Structure complete(boolean complete);

}
