package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A query that relates to a game element and another object. The other object
/// is usually a game element, but may be something else, typically the target
/// of an action which doesn't target a game element.
public interface TargetQuery<V> extends SubjectQuery<V> {

    /// The object this query targets.
    Object target();

    /// An event query that relates to two game elements.
    interface Event extends TargetQuery<None>, SubjectQuery.Event {}

    /// A validation query that relates to two game elements.
    interface Validation extends TargetQuery<Boolean>, SubjectQuery.Validation {}

}
