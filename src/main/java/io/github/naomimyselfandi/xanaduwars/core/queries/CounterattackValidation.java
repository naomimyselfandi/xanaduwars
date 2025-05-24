package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Action;
import io.github.naomimyselfandi.xanaduwars.core.Node;
import io.github.naomimyselfandi.xanaduwars.core.Unit;

/// A query that validates whether a unit can attack another.
public record CounterattackValidation(Unit initiator, Unit counterattacker)
        implements ActionTargetValidation<Unit, Node> {

    @Override
    public Action<Unit, Node> action() {
        return initiator.gameState().ruleset().attackAction();
    }

    @Override
    public Unit target() {
        return initiator;
    }

    @Override
    public Unit subject() {
        return counterattacker;
    }

}
