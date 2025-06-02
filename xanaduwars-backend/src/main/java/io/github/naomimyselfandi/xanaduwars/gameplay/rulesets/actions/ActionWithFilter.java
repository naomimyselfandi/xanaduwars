package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.Actor;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;

/// An action with a usage filter.
public interface ActionWithFilter<S extends Actor, T> extends Action<S, T> {

    /// The filter that defines valid inputs to this action.
    BiFilter<S, T> filter();

}
