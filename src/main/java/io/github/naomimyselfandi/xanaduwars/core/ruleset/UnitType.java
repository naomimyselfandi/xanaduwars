package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.DamageKey;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTypeId;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

/// A type of unit.
public non-sealed interface UnitType extends AssetType, DamageKey, Rule {

    /// This unit type's ID.
    UnitTypeId getId();

    /// Any tags that apply to this unit type.
    @Override @Unmodifiable Set<UnitTag> getTags();

    /// The base speed of a unit of this type.
    int getSpeed();

    /// Any weapons a unit of this type carries.
    @Unmodifiable List<Weapon> getWeapons();

}
