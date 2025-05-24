package io.github.naomimyselfandi.xanaduwars.core;

/// A query with a default value. This query can be evaluated without providing
/// an explicit initial value, in which case the default is used instead.
public interface DefaultQuery<V> extends Query<V> {

    /// This query's default value.
    V defaultValue();

}
