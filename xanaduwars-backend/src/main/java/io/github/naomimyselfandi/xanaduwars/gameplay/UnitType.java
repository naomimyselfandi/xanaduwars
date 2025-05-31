package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import jakarta.validation.constraints.Positive;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

/// A type of unit.
public non-sealed interface UnitType extends NodeType {

    /// This unit type's index. This is zero for the first unit type, one for
    /// the second unit type, and so on.
    @Override
    UnitTypeId id();

    /// The cost to deploy a unit of this type.
    @Override
    @Unmodifiable Map<Resource, @Positive Integer> costs();

    /// The vision range of a unit of this type.
    int vision();

    /// The base speed of a unit of this type.
    int speed();

    /// The range at which at a unit of this type can attack.
    Range range();

    /// This unit type's damage table. This influences which targets a unit of
    /// this type can attack and how much damage it inflicts.
    @Unmodifiable Map<NodeType, Scalar> damageTable();

    /// This unit type's hangar. If a unit has a tag which is in this set, it
    /// can be carried by a unit of this type.
    TagSet hangar();

    /// Any special abilities a unit of this type can use.
    @Unmodifiable List<Action<Unit, ?>> abilities();

}
