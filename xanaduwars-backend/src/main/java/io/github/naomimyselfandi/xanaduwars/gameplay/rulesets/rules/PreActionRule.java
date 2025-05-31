package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionValidation;

/// A rule that handles the standard pre-action checks.
public record PreActionRule() implements GameRule.Validator<ActionValidation> {

    @Override
    public boolean isValid(ActionValidation query) {
        var subject = query.subject();
        var gameState = subject.gameState();
        if (!gameState.activePlayer().equals(subject.owner().orElse(null))) {
            return false;
        } else if (subject instanceof Unit unit) {
            return unit.canAct() && unit.tile().isPresent();
        } else {
            return true;
        }
    }

}
