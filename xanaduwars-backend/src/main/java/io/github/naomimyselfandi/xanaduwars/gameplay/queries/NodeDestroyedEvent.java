package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;

/// An event indicating a unit or tile was destroyed.
public interface NodeDestroyedEvent<T extends Node> extends SubjectQuery.Event<T> {}
