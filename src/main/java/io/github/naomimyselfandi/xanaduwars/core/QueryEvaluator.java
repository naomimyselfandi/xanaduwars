package io.github.naomimyselfandi.xanaduwars.core;

import java.util.function.Consumer;

/// A component that evaluates queries by applying applicable game rules.
///
/// @apiNote This is effectively a message bus; the term "query" is used, by
/// analogy to SQL queries, to denote their ability to return values, unlike
/// most messaging systems.
public interface QueryEvaluator {

    /// Evaluate a query. The details of this process are described in [Rule].
    /// @param query The query to evaluate.
    /// @param value The query's default value.
    <Q extends Query<V>, V> V evaluate(Q query, V value);

    /// Evaluate a query using its default value.
    <V> V evaluate(DefaultQuery<V> query);

    /// Evaluate a validation query. If the validation fails due to a rule, the
    /// given callback is called with that rule.
    boolean evaluate(Validation query, Consumer<Rule<?, ?>> callback);

}
