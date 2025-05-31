package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ActionWithFilter;

/// A query that validates whether an element can use an action on a target.
/// To allow spells to force actions, this does not check whether the element
/// is in a valid state to take actions.
///
/// @param action  The action the element wants to use.
/// @param subject The element that wants to use the action.
/// @param target  The object the element wants to use the action on.
public record StandardTargetValidation<S extends Element, T>(
        @Override Action<S, T> action,
        @Override S subject,
        @Override T target
) implements ActionTargetValidation<S, T> {

    @Override
    public Boolean defaultValue() {
        return subject.gameState().actions(subject).contains(action)
                && action.test(subject, target)
                && (!(action instanceof ActionWithFilter<S, T> a) || a.filter().test(subject, target))
                && (!(target instanceof Unit unit) || specialConditionsHoldFor(unit));
    }

    private boolean specialConditionsHoldFor(Unit unit) {
        return unit.tile().isPresent() && subject.owner().filter(it -> it.canSee(unit)).isPresent();
    }

}
