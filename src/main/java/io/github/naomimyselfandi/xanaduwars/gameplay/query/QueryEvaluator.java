package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import java.util.function.Consumer;

/// A component that evaluates queries by applying applicable game rules.
///
/// @apiNote This is effectively a message bus; the term "query" is used, by
/// analogy to SQL queries, to denote their ability to return values, unlike
/// most messaging systems.
public interface QueryEvaluator {

    /// Evaluate a query. The details of this process are described in [Rule].
    /// @param query The query to evaluate.
    <Q extends Query<V>, V> V evaluate(Q query);

    /// Evaluate a query, inspecting the rules that handle it.
    /// @param query The query to evaluate.
    /// @param callback A callback to run for each rule that handles the query.
    <Q extends Query<V>, V> V evaluate(Q query, Consumer<Rule<?, ?>> callback);

}
