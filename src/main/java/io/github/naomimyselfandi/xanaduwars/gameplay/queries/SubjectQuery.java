package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.query.Query;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A query that relates to a single object.
public interface SubjectQuery<V, S> extends Query<V> {

    /// The game element this query is about.
    S subject();

    /// An event query that relates to a single game element.
    interface Event<S> extends SubjectQuery<None, S>, io.github.naomimyselfandi.xanaduwars.gameplay.query.Event {}

    /// A validation query that relates to a single game element.
    interface Validation<S> extends SubjectQuery<Boolean, S>, io.github.naomimyselfandi.xanaduwars.gameplay.query.Validation {}

}
