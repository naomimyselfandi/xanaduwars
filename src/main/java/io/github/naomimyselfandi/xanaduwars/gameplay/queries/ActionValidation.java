package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;

/// A query that validates whether an element can act.
public record ActionValidation(@Override Element subject) implements SubjectQuery.Validation<Element> {

    /// {@inheritDoc}
    /// @implSpec This implementation automatically checks if the element's
    /// owner is the active player.
    @Override
    public Boolean defaultValue() {
        if (subject instanceof Unit unit && !unit.canAct()) {
            return false;
        } else {
            return subject.gameState().activePlayer().equals(subject.owner().orElse(null));
        }
    }

}
