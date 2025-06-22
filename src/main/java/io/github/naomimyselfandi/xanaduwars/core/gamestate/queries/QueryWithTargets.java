package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A query with some number of targets. This interface allows scripts to refer
/// to the first target as `target` instead of `targets[0]`.
public interface QueryWithTargets<T> extends Query<T> {

    /// This query's targets.
    @Unmodifiable List<?> targets();

    /// This query's first target, or `null` if this query has no targets.
    default @Nullable Object target() {
        var targets = targets();
        return targets.isEmpty() ? null : targets.getFirst();
    }

}
