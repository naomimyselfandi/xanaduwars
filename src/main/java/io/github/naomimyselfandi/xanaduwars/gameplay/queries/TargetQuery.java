package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A query that relates to two objects. By convention, the first object is
/// called the *subject*, and the second is called the *target*. This reflects
/// a common use of target queries, but is not normative.
public interface TargetQuery<V, S, T> extends SubjectQuery<V, S> {

    /// The object this query targets.
    T target();

    /// An event query that relates to two game elements.
    interface Event<S, T> extends TargetQuery<None, S, T>, SubjectQuery.Event<S> {}

    /// A validation query that relates to two game elements.
    interface Validation<S, T> extends TargetQuery<Boolean, S, T>, SubjectQuery.Validation<S> {}

}
