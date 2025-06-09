package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
class QueryEvaluatorImpl implements QueryEvaluator {

    private static final String VALUE = "value";

    private final ContextFactory contextFactory;
    private final ScriptSelector scriptSelector;
    private final ConversionService conversionService;

    @Override
    public <T> T evaluate(GlobalRuleSource globals, Query<T> query) {
        var context = contextFactory.create(globals, query);
        var scripts = scriptSelector.select(globals, query);
        return evaluate(context, query, scripts);
    }

    private <T> T evaluate(EvaluationContext context, Query<T> query, List<Script> scripts) {
        var type = type(query);
        var value = query.defaultValue();
        for (var script : scripts) {
            context.setVariable(VALUE, value);
            var rawValue = script.run(context);
            if (rawValue != null && type != None.class) {
                value = Objects.requireNonNull(conversionService.convert(rawValue, type));
            }
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
