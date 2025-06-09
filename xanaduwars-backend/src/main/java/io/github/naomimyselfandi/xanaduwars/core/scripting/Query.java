package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/// A query used during gameplay. Queries are used to notify rules of game
/// events, but also to calculate values which rules may want to modify. The
/// word "query" is used by analogy to SQL queries, since they can likewise both
/// calculate values and modify data.
/// @param <T> The type of value produced when this query is evaluated.
@ExcludeFromCoverageReport
public interface Query<T> {

    /// The subject of this query. If this is a [rule source][RuleSource], the
    /// rules it provides apply to the query after the global rules.
    @Nullable Object subject();

    /// The value this query yields if no rules intervene.
    T defaultValue();

    /// Any scripts that should be run before evaluating this query.
    default Stream<Script> prologue() {
        return Stream.empty();
    }

    /// Any scripts that should be run after evaluating this query.
    default Stream<Script> epilogue() {
        return Stream.empty();
    }

}
