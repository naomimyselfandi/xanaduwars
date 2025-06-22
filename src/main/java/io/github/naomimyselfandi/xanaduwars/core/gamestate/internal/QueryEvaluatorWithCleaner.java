package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.CleanupEvent;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;

record QueryEvaluatorWithCleaner(QueryEvaluator queryEvaluator, Cleaner cleaner) implements QueryEvaluator {

    @Override
    public <T> T evaluate(Object game, Query<T> query) {
        var result = queryEvaluator.evaluate(game, query);
        if (query instanceof CleanupEvent && game instanceof GameState it) cleaner.clean(it);
        return result;
    }

}
