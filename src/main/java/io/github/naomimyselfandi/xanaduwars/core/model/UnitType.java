package io.github.naomimyselfandi.xanaduwars.core.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A type of unit.
public interface UnitType extends Specification, UnitSelector {

    /// Get this unit type's unique name.
    @Override
    String getName();

    /// Get any tags that normally apply to units of this type.
    @Unmodifiable List<UnitTag> getTags();

    /// Get the default speed of a unit of this type.
    int getSpeed();

    /// Get the default perception range of a unit of this type.
    int getPerception();

    /// Get the default maximum HP of a unit of this type.
    int getMaxHp();

    /// Get the supply cost to deploy a unit of this type.
    int getSupplyCost();

    /// Get the aether cost to deploy a unit of this type.
    int getAetherCost();

    /// Describe the units a unit of this type can usually carry.
    @Unmodifiable UnitSelector getHangar();

    /// Get the abilities a unit of this type can normally use.
    @Unmodifiable List<Ability> getAbilities();

    /// Get the weapons a unit of this type normally carries.
    @Unmodifiable List<Weapon> getWeapons();

}
