package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A query that tests whether an element can use some actions together.
public record ActionGroupQuery(Element subject, @Unmodifiable List<Action> actions) implements Query<Result> {

    /// A query that tests whether an element can use some actions together.
    /// @implSpec This constructor takes an immutable copy of its list argument.
    public ActionGroupQuery(Element subject, List<Action> actions) {
        this.subject = subject;
        this.actions = List.copyOf(actions);
    }

    @Override
    @SuppressWarnings("SlowListContainsAll") // List is tiny
    public Result defaultValue() {
        if (subject.getActions().containsAll(actions)) {
            return Result.okay();
        } else {
            return Result.fail("Unknown action.");
        }
    }

}
