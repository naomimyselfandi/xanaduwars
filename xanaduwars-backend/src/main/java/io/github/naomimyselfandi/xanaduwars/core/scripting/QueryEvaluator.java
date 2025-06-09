package io.github.naomimyselfandi.xanaduwars.core.scripting;

/// A service that can evaluate queries. Interacting directly with this service
/// should be rare; it's ordinarily simpler to ask a game state to evaluate a
/// query instead.
public interface QueryEvaluator {

    /// Evaluate a query. Every rule applying to the query is checked, starting
    /// with the global rules. If the query specifies a prologue or epilogue,
    /// they are automatically run at the appropriate time.
    <T> T evaluate(GlobalRuleSource globals, Query<T> query);

}
