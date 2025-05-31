package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ability;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.SimpleActionMixin;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;
import io.github.naomimyselfandi.xanaduwars.ext.DeserializationHelper;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;

/// An action representing a unit's special ability.
@JsonCommentable
@DeserializationHelper
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "ability")
public interface Ability<T> extends Action<Unit, T> {

    /// A helper for implementing non-generic abilities.
    @ConvenienceMixin
    sealed interface Simple<T> extends SimpleActionMixin<Unit, T>, Ability<T> permits BuildAbility, RepairAbility {}


}
