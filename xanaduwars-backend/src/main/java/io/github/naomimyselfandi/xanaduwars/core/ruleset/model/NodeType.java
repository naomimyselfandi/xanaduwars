package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

/// A type of element that can contain units.
public sealed interface NodeType extends PhysicalType permits TileType, UnitType {}
