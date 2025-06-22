package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class QueryEvaluatorImpl implements QueryEvaluator {

    private static final String VALUE = "value";

    private final GlobalRuleSource globals;
    private final ContextFactory contextFactory;
    private final ScriptSelector scriptSelector;

    @Override
    public <T> T evaluate(Object game, Query<T> query) {
        var context = contextFactory.create(globals, query, game);
        var scripts = scriptSelector.select(globals, query);
        var type = type(query);
        var value = query.defaultValue();
        for (var script : scripts) {
            context.setVariable(VALUE, value);
            var rawValue = script.run(context, type);
            if (rawValue != null) value = type.cast(rawValue);
            if (value instanceof Result.Fail) break;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> type(Query<T> query) {
        return (Class<T>) TypeFactory
                .defaultInstance()
                .constructType(query.getClass())
                .findTypeParameters(Query.class)[0]
                .getRawClass();
    }

}
