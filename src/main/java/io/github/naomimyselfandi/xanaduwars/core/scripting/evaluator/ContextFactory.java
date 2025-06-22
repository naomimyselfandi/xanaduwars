package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import org.springframework.expression.EvaluationContext;

interface ContextFactory {

    EvaluationContext create(GlobalRuleSource globals, Query<?> query, Object game);

}
