package io.github.naomimyselfandi.xanaduwars.gameplay.value;

/// The ID of an element which can take actions.
public sealed interface ActorId extends ElementId permits PlayerId, NodeId {}
