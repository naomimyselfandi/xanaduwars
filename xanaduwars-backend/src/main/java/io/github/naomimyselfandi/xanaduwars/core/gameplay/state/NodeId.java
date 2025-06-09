package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

/// The ID of an element that can contain units.
public sealed interface NodeId extends ElementId permits TileId, UnitId {}
