package io.github.naomimyselfandi.xanaduwars.gameplay.value;

/// The ID of a unit or tile.
public sealed interface NodeId extends ActorId permits TileId, UnitId {}
