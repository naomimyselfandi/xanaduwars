package io.github.naomimyselfandi.xanaduwars.core.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/// A unit's weapon.
/// @param name The weapon's name. This is primarily used for display purposes,
/// and is not necessarily unique.
/// @param damage How much damage this weapon inflicts to various targets.
/// @param minRange This weapon's minimum range (inclusive).
/// @param maxRange This weapon's maximum range (inclusive).
public record Weapon(String name, @Unmodifiable Map<UnitTag, Integer> damage, int minRange, int maxRange) {

    /// A unit's weapon.
    /// @param name The weapon's name. This is primarily used for display
    /// purposes, and is not necessarily unique.
    /// @param damage How much damage this weapon inflicts to various targets.
    /// @param minRange This weapon's minimum range (inclusive).
    /// @param maxRange This weapon's maximum range (inclusive).
    public Weapon(String name, Map<UnitTag, Integer> damage, int minRange, int maxRange) {
        this.name = name;
        this.damage = Map.copyOf(damage);
        this.minRange = Math.max(minRange, 1);
        this.maxRange = Math.max(maxRange, 1);
    }

}
