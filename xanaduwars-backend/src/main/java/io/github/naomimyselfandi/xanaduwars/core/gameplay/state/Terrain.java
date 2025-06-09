package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.MovementTable;

public sealed interface Terrain extends Physical permits Structure, Tile {

    /// This terrain's movement table. When a unit tries to enter this terrain,
    /// if any of its tags are keys into this map, it may enter the terrain by
    /// expending an amount of speed equal to the corresponding value.
    /// Otherwise, it may not enter the terrain.
    MovementTable movementTable();

    /// The defensive cover applied by this terrain.
    double cover();

}
