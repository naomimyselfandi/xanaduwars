package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import org.jetbrains.annotations.Unmodifiable;

/// A type of terrain. Tiles and structures are both considered terrain.
public sealed interface TerrainType extends PhysicalType permits StructureType, TileType {

    /// This terrain type's movement table. When a unit tries to enter terrain
    /// of this type, if any of its tags are keys into this map, it may enter
    /// the terrain by expending an amount of speed equal to the corresponding
    /// value. Otherwise, it may not enter the terrain.
    @Unmodifiable MovementTable movementTable();

    /// The defensive cover applied by terrain of this type.
    double cover();

}
