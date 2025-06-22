package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;

import java.util.List;

/// A query that tests whether an element can use an action. Note that this
/// does not consider whether the actor is in a legal state to act.
public record ActionUsageQuery(Element subject, Action action) implements Query<Result> {

    @Override
    public Result defaultValue() {
        return subject.getActions().contains(action) ? Result.okay() : Result.fail("Invalid action.");
    }

    @Override
    public List<Script> epilogue() {
        return List.of(action.getPrecondition());
    }

}
