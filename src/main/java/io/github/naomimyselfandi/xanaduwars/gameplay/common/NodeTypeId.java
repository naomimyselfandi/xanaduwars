package io.github.naomimyselfandi.xanaduwars.gameplay.common;

/// The ID of a unit or tile type.
public sealed interface NodeTypeId extends TypeId permits TileTypeId, UnitTypeId {}
