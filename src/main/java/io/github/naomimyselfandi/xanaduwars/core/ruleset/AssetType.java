package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A type of element that is owned by a player. Assets also have HP and can be
/// attacked, damaged, and destroyed. Asset types are also actions which create
/// an appropriate element when used.
public sealed interface AssetType extends Action, Declaration permits StructureType, UnitType {

    /// How far an asset of this type can see.
    int getVision();

    /// Any actions an asset of this type can take.
    @Unmodifiable List<Action> getActions();

    /// The supply cost to create an asset of this type.
    @Override
    FixedCost getSupplyCost();

    /// The aether cost to create an asset of this type.
    @Override
    FixedCost getAetherCost();

}
