package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Hangar;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Name;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

/// A unit in a game state.
public non-sealed interface Unit extends Asset, Node {

    /// This unit's ID. Unit IDs should be regarded as somewhat sensitive
    /// since they can be used to infer hidden information; for example, if a
    /// player loses and regains vision of an enemy unit, if they can peek at
    /// its ID, they will know that it's the same unit instead of another unit
    /// of the same type.
    UnitId id();

    /// The type of unit this is.
    UnitType type();

    /// Any tags that apply to this unit.
    Set<UnitTag> tags();

    /// The tile this unit is on or the unit carrying it.
    Node location();

    /// The tile this unit is on or the unit carrying it.
    Unit location(Node location);

    /// The tile this unit is on.
    @Override
    @Nullable Tile tile();

    /// The unit this unit is carrying, if any.
    @Nullable Unit cargo();

    /// This unit's hangar.
    Hangar hangar();

    /// This unit's base speed.
    int speed();

    /// The names of the actions this unit has taken this turn.
    @Unmodifiable List<Name> actionsThisTurn();

    /// The names of the actions this unit has taken this turn.
    Unit actionsThisTurn(List<Name> actionsThisTurn);

}
