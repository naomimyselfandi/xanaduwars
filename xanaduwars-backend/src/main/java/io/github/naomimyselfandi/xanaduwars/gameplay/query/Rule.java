package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

/// A game rule, which acts on a specific type of [query][Query]. They may have
/// side effects or modify the query's return value, as appropriate for the rule
/// and query.
///
/// When a query is evaluated, a default value is provided. If no rules apply,
/// this value is returned unchanged. Otherwise, all rules for that query's type
/// are evaluated in declaration order. Each rule receives the query and the
/// most recently calculated value - either the default value or the result of a
/// prior rule. The final return value, after all applicable rules have run,
/// becomes the result of the query.
public interface Rule<Q extends Query<V>, V> {

    /// Determine if this rule applies to the given query and value.
    /// @param query The query.
    /// @param value The query's current return value.
    boolean handles(Q query, V value);

    /// Applies this rule to the given query. The return value becomes the new
    /// result of the query and is passed to subsequent rules.
    /// @param query The query.
    /// @param value The most recently calculated value.
    /// @return The query's new value.
    /// @implNote Implementations may assume [#handles(Query, Object)] has
    /// previously returned `true` for the given input.
    V handle(Q query, V value);

    /// Get the type of query this rule handles.
    /// @implSpec The default implementation uses reflection to determine the
    /// query type. Concrete subclasses with generic type parameters must
    /// override this method explicitly.
    @ExcludeFromCoverageReport
    default Class<Q> queryType() {
        // Delegating to a helper to mitigate the Mockito coverage report bug.
        return RuleHelper.queryType(this);
    }

}
