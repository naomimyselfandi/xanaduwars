package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/// An ability which allows a unit to attack other units.
public interface Weapon extends Action {

    /// The damage this weapon inflicts to various targets.
    @Unmodifiable Map<WeaponTarget, Integer> damage();

}
