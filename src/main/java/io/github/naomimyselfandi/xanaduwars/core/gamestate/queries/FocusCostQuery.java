package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A query that calculates an action's focus cost.
public record FocusCostQuery(Element subject, Action action, @Unmodifiable List<Object> targets)
        implements QueryWithTargets<Integer> {

    /// A query that calculates an action's focus cost.
    /// @implSpec This constructor takes an immutable copy of its list argument.
    public FocusCostQuery(Element subject, Action action, List<Object> targets) {
        this.subject = subject;
        this.action = action;
        this.targets = List.copyOf(targets);
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }

    @Override
    public List<Script> prologue() {
        return List.of(action.getFocusCost());
    }

}
