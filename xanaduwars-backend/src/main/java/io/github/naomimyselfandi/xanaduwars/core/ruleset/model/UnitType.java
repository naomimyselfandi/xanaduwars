package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

/// A type of unit.
public non-sealed interface UnitType extends AssetType, NodeType, WeaponTarget {

    /// This unit type's ID.
    UnitTypeId getId();

    /// Any tags that apply to this unit type.
    @Override @Unmodifiable Set<UnitTag> getTags();

    /// The base speed of a unit of this type.
    int getSpeed();

    /// The vision range of a unit of this type.
    int getVision();

    /// The weapons carried by units of this type, if any.
    List<Weapon> getWeapons();

    /// Any special abilities a unit of this type can use.
    @Unmodifiable List<Action> getAbilities();

    /// Any unit types a unit of this type can carry.
    Hangar getHangar();

}
