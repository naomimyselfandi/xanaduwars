package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/// A type of structure. A structure is built on top of a tile and replaces its
/// movement table and defensive cover.
public non-sealed interface StructureType extends AssetType, TerrainType {

    /// This structure type's ID.
    StructureTypeId getId();

    /// Any tags that apply to this structure type.
    @Override @Unmodifiable Set<StructureTag> getTags();

    /// How long this structure takes to build.
    int getBuildTime();

}
