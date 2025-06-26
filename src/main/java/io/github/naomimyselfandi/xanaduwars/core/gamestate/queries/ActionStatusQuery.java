package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

/// A query that checks whether an element can currently act.
public record ActionStatusQuery(Element subject) implements Query<Result> {

    @Override
    public Result defaultValue() {
        if (subject.getGameState().getActivePlayer().equals(subject.getOwner().orElse(null))) {
            return Result.okay();
        } else {
            return Result.fail("Cannot issue commands to that.");
        }
    }

}
