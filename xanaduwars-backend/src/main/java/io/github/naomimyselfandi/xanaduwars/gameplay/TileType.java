package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Tag;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TileTypeId;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/// A type of tile. Tiles control how units move, and also provide defensive
/// cover to units on them. Some tile types also describe *structures*, which
/// can be built on other tiles, replacing their original type until the
/// structure is destroyed.
public non-sealed interface TileType extends NodeType {

    /// This tile type's index. This is zero for the first tile type, one for
    /// the second tile type, and so on.
    @Override
    TileTypeId id();

    /// This tile type's movement table. This influences which units can enter
    /// a tile of this type, and how much doing so counts against their speed.
    @Unmodifiable Map<Tag, @Positive Double> movementTable();

    /// The cover provided by a tile of this type.
    /// @see Tile#cover() The interpretation of this percent is described in the
    /// documentation for the `Tile` interface.
    Percent cover();

    /// The type of tile a structure of this type can be built on. This is
    /// present if and only if this tile type describes a structure.
    Optional<TileType> foundation();

    /// The cost to deploy or build a structure of this type. This is empty for
    /// tile types which do not describe structures.
    @Override
    @Unmodifiable Map<Resource, @Positive Integer> costs();

    /// The time, in turns, needed to build a structure of this type. This is
    /// zero if and only if this tile type does not describe a structure.
    @PositiveOrZero int buildTime();

    /// The resources a tile of this type provides every turn.
    @Unmodifiable Map<Resource, @Positive Integer> income();

    /// The types of unit a tile of this type can deploy.
    @Unmodifiable Set<UnitType> deploymentRoster();

}
