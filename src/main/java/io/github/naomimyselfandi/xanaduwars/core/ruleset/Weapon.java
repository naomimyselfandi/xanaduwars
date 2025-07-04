package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.ActionTag;
import io.github.naomimyselfandi.xanaduwars.core.common.DamageKey;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Set;

/// An ability which allows a unit to attack other units.
public non-sealed interface Weapon extends Action {

    /// The damage this weapon inflicts to various targets.
    @Unmodifiable Map<DamageKey, Integer> getDamage();

    /// Get any tags which apply to this weapon.
    @Unmodifiable Set<ActionTag> getTags();

}
