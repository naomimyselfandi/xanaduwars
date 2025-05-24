package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.Query;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A query that relates to a single game element.
public interface SubjectQuery<V> extends Query<V> {

    /// The game element this query is about.
    Element subject();

    /// An event query that relates to a single game element.
    interface Event extends SubjectQuery<None>, io.github.naomimyselfandi.xanaduwars.core.Event {}

    /// A validation query that relates to a single game element.
    interface Validation extends SubjectQuery<Boolean>, io.github.naomimyselfandi.xanaduwars.core.Validation {}

}
