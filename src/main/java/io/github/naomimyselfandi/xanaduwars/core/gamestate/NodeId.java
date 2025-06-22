package io.github.naomimyselfandi.xanaduwars.core.gamestate;

/// The ID of a tile or unit.
public sealed interface NodeId extends ElementId permits TileId, UnitId {}
