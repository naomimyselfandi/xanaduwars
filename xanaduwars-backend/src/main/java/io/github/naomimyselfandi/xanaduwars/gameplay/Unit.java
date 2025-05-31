package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

/// A unit. A unit can move, attack, use special abilities, and carry other
/// units, depending on the type of unit it is.
public non-sealed interface Unit extends Node {

    /// The type of unit this is.
    @Override
    UnitType type();

    /// This unit's ID. Unit IDs reflect the order in which they were created:
    /// given two units, the unit with the lower ID was created first. The first
    /// unit created in any given game has an ID of zero.
    ///
    /// @apiNote Unit IDs are somewhat sensitive. Suppose that a player sees an
    /// enemy unit, then loses vision of it. If they later see a unit of the
    /// same type, if they can discern the unit ID, they can tell if they are
    /// the same units or different units, potentially giving them insight into
    /// their opponent's army composition that they shouldn't have.
    UnitId id();

    /// This tile this unit is on, or the unit carrying this unit.
    Node location();

    /// This tile this unit is on, or the unit carrying this unit.
    void location(Node location);

    /// Any units carried by this unit.
    @Unmodifiable List<Unit> cargo();

    /// Whether this unit can currently act.
    boolean canAct();

    /// Whether this unit can currently act.
    void canAct(boolean canAct);

    /// How far this unit can see.
    int vision();

    /// This unit's base speed.
    int speed();

    /// The range at which this unit can attack.
    Range range();

    /// The base damage this unit inflicts to various targets.
    Map<NodeType, Scalar> damageTable();

    /// This unit's hangar. If a unit has a tag in this set, this unit can carry
    /// it.
    TagSet hangar();

    /// Any special abilities this unit can use.
    List<Action<Unit, ?>> abilities();

}
