package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

/// The ID of an element that can take actions.
public sealed interface ActorId extends ElementId permits AssetId, PlayerId {}
