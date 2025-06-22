package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Event;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// An event that causes an actor to take an action.
public record ActionExecutionEvent(Element subject, Action action, @Unmodifiable List<Object> targets)
        implements Event<Result>, QueryWithTargets<Result> {

    /// An event that causes an actor to take an action.
    /// @implSpec This constructor takes an immutable copy of its list argument.
    public ActionExecutionEvent(Element subject, Action action, List<Object> targets) {
        this.subject = subject;
        this.action = action;
        this.targets = List.copyOf(targets);
    }

    @Override
    public Result defaultValue() {
        return Result.okay();
    }

    @Override
    public List<Script> epilogue() {
        return List.of(action.getEffect());
    }

}
