package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;

/// A query that validates whether a unit can attack another.
public record CounterattackValidation(Unit initiator, Unit counterattacker)
        implements ActionTargetValidation<Unit, Node> {

    @Override
    public Action<Unit, Node> action() {
        return initiator.gameState().ruleset().details().attackAction();
    }

    @Override
    public Unit target() {
        return initiator;
    }

    @Override
    public Unit subject() {
        return counterattacker;
    }

    @Override
    public Boolean defaultValue() {
        return !counterattacker.hp().equals(Percent.ZERO);
    }

}
