package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/// A structure in a game.
public non-sealed interface Structure extends Asset {

    /// Get this structure's ID. A structure's ID is derived from the ID of the
    /// tile the structure is on.
    @Override
    StructureId getId();

    /// Get this structure's type.
    @Override
    StructureType getType();

    /// Set this structure's type.
    Structure setType(StructureType type);

    /// Get any tags that apply to this structure.
    @Unmodifiable Set<StructureTag> getTags();

    /// Get the tile this structure is on.
    @Override
    Optional<Tile> getTile();

    /// Check if this structure is incomplete. An incomplete structure cannot
    /// take actions and does not influence the terrain.
    boolean isIncomplete();

    /// Set whether this structure is incomplete. An incomplete structure cannot
    /// take actions and does not influence the terrain.
    Structure setIncomplete(boolean incomplete);

}
