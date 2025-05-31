package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;

/// A query that runs cleanup after an action. If the subject is a unit which
/// took multiple actions simultaneously, only one event is posted for all of
/// those actions.
public record ActionCleanupEvent(@Override Element subject) implements SubjectQuery.Event<Element> {}
