package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;

/// A factory for query evaluators.
@ExcludeFromCoverageReport
public interface QueryEvaluatorFactory {

    /// Create a query evaluator for a ruleset.
    QueryEvaluator create(GlobalRuleSource globalRuleSource);

}
