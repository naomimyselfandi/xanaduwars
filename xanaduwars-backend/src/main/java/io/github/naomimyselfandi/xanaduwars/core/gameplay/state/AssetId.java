package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

/// The ID of an element that is owned by a player. Assets also have HP and
/// can be attacked, damaged, and destroyed.
public sealed interface AssetId extends ActorId permits UnitId, StructureId {}
