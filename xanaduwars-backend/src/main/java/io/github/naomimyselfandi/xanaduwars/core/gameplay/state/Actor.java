package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.RuleSource;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A game state element that can take actions.
public sealed interface Actor extends Element, RuleSource permits Asset, Player {

    /// The actions this actor can take.
    @Unmodifiable List<Action> getAction();

    /// The player who owns this actor. (Players are considered to own
    /// themselves.)
    @Nullable Player getOwner();

    /// Check if two actors are on the same team.
    boolean isFriend(@Nullable Actor actor);

    /// Check if two actors are on different teams.
    boolean isFoe(@Nullable Actor actor);

    /// Check if two actors belong to (or are) the same player.
    boolean isSelf(@Nullable Actor actor);

}
