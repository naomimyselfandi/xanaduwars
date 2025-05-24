package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Node;

/// An event indicating a unit or tile was destroyed.
public record NodeDestroyedEvent(@Override Node subject) implements SubjectQuery.Event {}
