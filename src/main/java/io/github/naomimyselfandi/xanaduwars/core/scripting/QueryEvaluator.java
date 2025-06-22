package io.github.naomimyselfandi.xanaduwars.core.scripting;

/// An object that can evaluate game queries.
public interface QueryEvaluator {

    /// Evaluate a query. Every rule applying to the query is checked, starting
    /// with the global rules. If the query specifies a prologue and/or
    /// epilogue, they are automatically run at the appropriate time.
    <T> T evaluate(Object game, Query<T> query);

}
