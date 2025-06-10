package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

/// A type of structure. A structure is built on top of a tile and replaces its
/// movement table and defensive cover.
public non-sealed interface StructureType extends AssetType, TerrainType {

    /// This structure type's ID.
    StructureTypeId getId();

    /// Any tags that apply to this structure type.
    @Override @Unmodifiable Set<StructureTag> getTags();

    /// The type of tile that a structure of this type can be built on.
    TileType getFoundation();

    /// The units a structure of this type can deploy.
    @Unmodifiable List<UnitType> getDeploymentRoster();

    /// The supplies a structure of this type grants every turn.
    int getSupplyIncome();

    /// The aether a structure of this type grants every turn.
    int getAetherIncome();

    /// How long this structure takes to build.
    int getBuildTime();

    /// How far a structure of this type can see.
    int getVision();

}
