package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/// An asset in a game.
public non-sealed interface Unit extends Asset, Node {

    /// This unit's unique ID. Unit IDs correspond to their creation order:
    /// given a pair of units (in the same game), the unit with the lower ID is
    /// older.
    @Override
    UnitId getId();

    /// Get this unit's type.
    @Override
    UnitType getType();

    /// Set this unit's type.
    Unit setType(UnitType type);

    /// Get any tags that apply to this unit.
    @Unmodifiable Set<UnitTag> getTags();

    /// Get this unit's base speed.
    int getSpeed();

    /// Get the tile this unit is on or the unit carrying it.
    Node getLocation();

    /// Get the tile this unit is on. This is `null` if and only if this unit is
    /// being carried by another unit.
    @Override
    @Nullable Tile getTile();

    /// Get the unit this unit is carrying, if any.
    @Nullable Unit getCargo();

    /// Check if this unit is ready to act.
    boolean isReady();

    /// Set whether this unit is ready to act.
    Unit setReady(boolean ready);

}
