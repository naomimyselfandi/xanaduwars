package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

/// A type of structure. A structure is built on top of a tile and replaces its
/// movement table and defensive cover.
public non-sealed interface StructureType extends AssetType, TerrainType {

    /// This structure type's ID.
    StructureTypeId id();

    /// Any tags that apply to this structure type.
    @Override @Unmodifiable Set<StructureTag> tags();

    /// The type of tile that a structure of this type can be built on.
    TileType foundation();

    /// The units a structure of this type can deploy.
    @Unmodifiable List<UnitType> deploymentRoster();

    /// The supplies a structure of this type grants every turn.
    int supplyIncome();

    /// The aether a structure of this type grants every turn.
    int aetherIncome();

    /// How long this structure takes to build.
    int buildTime();

    /// How far a structure of this type can see.
    int vision();

}
