package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

/// A type of element that is owned by a player. Assets also have HP and can be
/// attacked, damaged, and destroyed.
public sealed interface AssetType extends ActorType, PhysicalType permits StructureType, UnitType {

    /// The supply cost to create an asset of this type.
    int supplyCost();

    /// The aether cost to create an asset of this type.
    int aetherCost();

}
