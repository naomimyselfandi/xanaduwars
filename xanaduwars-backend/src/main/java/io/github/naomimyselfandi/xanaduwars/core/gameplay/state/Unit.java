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
    UnitId getId();

    /// The type of unit this is.
    UnitType getType();

    /// Any tags that apply to this unit.
    Set<UnitTag> getTags();

    /// The tile this unit is on or the unit carrying it.
    Node getLocation();

    /// Move this unit to another tile or into another unit.
    Unit setLocation(Node location);

    /// The tile this unit is on.
    @Override
    @Nullable Tile getTile();

    /// The unit this unit is carrying, if any.
    @Nullable Unit getCargo();

    /// This unit's hangar.
    Hangar getHangar();

    /// This unit's base speed.
    int getSpeed();

    /// The names of the actions this unit has taken this turn.
    @Unmodifiable List<Name> getHistory();

    /// The names of the actions this unit has taken this turn.
    Unit setHistory(List<Name> actionsThisTurn);

}
