package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// A query used during gameplay. Queries are used to notify rules of game
/// events, but also to calculate values which rules may want to modify. The
/// word "query" is used by analogy to SQL queries, since they can likewise both
/// calculate values and modify data.
///
/// If a query represents or may cause a change to a game, it must also
/// implement the [Event] interface.
///
/// @param <T> The type of value produced when this query is evaluated.
@ExcludeFromCoverageReport
public interface Query<T> {

    /// The subject of this query. If this is a [rule source][RuleSource], the
    /// rules it provides apply to the query after the global rules.
    default @Nullable Object subject() {
        return null;
    }

    /// The value this query yields if no rules intervene.
    T defaultValue();

    /// Any scripts that should be run before evaluating this query.
    default List<Script> prologue() {
        return List.of();
    }

    /// Any scripts that should be run after evaluating this query.
    default List<Script> epilogue() {
        return List.of();
    }

}
