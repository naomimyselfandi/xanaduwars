package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Action;
import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.actions.ActionWithFilter;

/// A query that validates whether an element can use an action on a target.
/// To allow spells to force actions, this does not check whether the element
/// is in a valid state to take actions.
///
/// @param action  The action the element wants to use.
/// @param subject The element that wants to use the action.
/// @param target  The object the element wants to use the action on.
public record StandardTargetValidation<S extends Element, T>(
        @Override Action<S, T> action,
        @Override S subject, @Override T target
) implements ActionTargetValidation<S, T> {

    @Override
    public Boolean defaultValue() {
        return action.test(subject, target) && switch (action) {
            case ActionWithFilter<S, T> a -> a.filter().test(subject, target);
            default -> true;
        };
    }

}
