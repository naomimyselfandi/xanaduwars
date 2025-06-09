package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.springframework.expression.EvaluationContext;

interface ContextFactory {

    EvaluationContext create(GlobalRuleSource globals, Query<?> query);

}
