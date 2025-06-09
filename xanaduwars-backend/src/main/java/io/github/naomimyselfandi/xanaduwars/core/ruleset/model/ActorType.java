package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

/// A type of element that can take actions.
public sealed interface ActorType extends Declaration permits AssetType, Commander {}
