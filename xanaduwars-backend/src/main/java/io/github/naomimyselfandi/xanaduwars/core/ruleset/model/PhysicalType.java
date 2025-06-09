package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

/// A type of element with a location on the game map.
public sealed interface PhysicalType extends Declaration permits AssetType, NodeType, TerrainType {}
