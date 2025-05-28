package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A game query. Evaluating a query may change the game state, return a value,
/// or do both, depending on the game rules that handle it. This makes game
/// rules very flexible: the same mechanism can define both conventional event
/// handlers and distributed, collaborative calculations.
///
/// @param <V> The type returned by evaluating this query. For messages that
/// don't meaningfully return anything, use [None].
public interface Query<V> {

    /// Determine whether a value should short circuit query evaluation.
    ///
    /// This is typically used for "unanimous vote" queries, where a single
    /// `false` result should terminate evaluation. For convenience, see the
    /// [Validation] mixin interface.
    @ExcludeFromCoverageReport
    default boolean shouldShortCircuit(V value) {
        return false;
    }

    /// This query's default value. This value is returned if no rules handle
    /// the query.
    V defaultValue();

}
