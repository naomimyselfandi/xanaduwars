package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.RuleSource;
import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/// An element of a game state. Players, structures, tiles, and units are all
/// elements.
@ExcludeFromCoverageReport // Work around Mockito coverage report bug
public sealed interface Element extends RuleSource permits Physical, Player {

    /// Get the game state this element is part of.
    GameState getGameState();

    /// Get this element's unique ID.
    ElementId getId();

    /// Get this element's owner. Players are considered to own themselves.
    Optional<Player> getOwner();

    /// Check if two elements have the same owner.
    default boolean hasSameOwner(Element that) {
        return getOwner().flatMap(owner -> that.getOwner().filter(owner::equals)).isPresent();
    }

    /// Check if two elements are on the same team.
    default boolean isAlly(Element that) {
        return getOwner()
                .map(Player::getTeam)
                .flatMap(team -> that.getOwner().map(Player::getTeam).filter(team::equals))
                .isPresent();
    }

    /// Check if two elements are on different teams.
    default boolean isEnemy(Element that) {
        return getOwner()
                .map(Player::getTeam)
                .flatMap(team -> that.getOwner().map(Player::getTeam).filter(Predicate.not(team::equals)))
                .isPresent();
    }

    /// Get the actions this element can use.
    @Unmodifiable List<Action> getActions();

}
