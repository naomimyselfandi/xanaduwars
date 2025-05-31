package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.Element;

/// A query that validates whether an element can use an action on a target.
/// This does not consider whether the element can act at all;
/// use [ActionValidation] for that.
public sealed interface ActionTargetValidation<S extends Element, T> extends TargetQuery.Validation<S, T>
        permits CounterattackValidation, StandardTargetValidation {

    /// The action the element wants to use.
    Action<S, T> action();

    /// The element that wants to use the action.
    @Override
    S subject();

    /// The object the element wants to use the action on.
    @Override
    T target();

}
