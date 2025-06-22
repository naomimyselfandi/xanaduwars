package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class QueryEvaluatorFactoryImpl implements QueryEvaluatorFactory {

    private final ContextFactory contextFactory;
    private final ScriptSelector scriptSelector;

    @Override
    public QueryEvaluator create(GlobalRuleSource globalRuleSource) {
        return new QueryEvaluatorImpl(globalRuleSource, contextFactory, scriptSelector);
    }

}
