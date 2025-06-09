package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

/// A type of unit.
public non-sealed interface UnitType extends AssetType, NodeType, WeaponTarget {

    /// This unit type's ID.
    UnitTypeId id();

    /// Any tags that apply to this unit type.
    @Override @Unmodifiable Set<UnitTag> tags();

    /// The base speed of a unit of this type.
    int speed();

    /// The vision range of a unit of this type.
    int vision();

    /// The weapons carried by units of this type, if any.
    List<Weapon> weapons();

    /// Any special abilities a unit of this type can use.
    @Unmodifiable List<Action> abilities();

    /// Any unit types a unit of this type can carry.
    Hangar hangar();

}
