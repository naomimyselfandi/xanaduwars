package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.RuleSource;
import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// An element of a game state. Players, structures, tiles, and units are all
/// elements.
@ExcludeFromCoverageReport // Work around Mockito coverage report bug
public sealed interface Element extends RuleSource permits Physical, Player {

    /// Get the game state this element is part of.
    GameState getGameState();

    /// Get this element's unique ID.
    ElementId getId();

    /// Get this element's owner. Players are considered to own themselves.
    @Nullable Player getOwner();

    /// Check if two elements have the same owner.
    default boolean hasSameOwner(Element that) {
        return getOwner() instanceof Player player && player.equals(that.getOwner());
    }

    /// Check if two elements are on the same team.
    default boolean isAlly(Element that) {
        return this.getOwner() instanceof Player thisOwner
                && that.getOwner() instanceof Player thatOwner
                && thisOwner.getTeam().equals(thatOwner.getTeam());
    }

    /// Check if two elements are on different teams.
    default boolean isEnemy(Element that) {
        return this.getOwner() instanceof Player thisOwner
                && that.getOwner() instanceof Player thatOwner
                && !thisOwner.getTeam().equals(thatOwner.getTeam());
    }

    /// Get the actions this element can use.
    @Unmodifiable List<Action> getActions();

}
