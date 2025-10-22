package io.github.naomimyselfandi.xanaduwars.core.model;

import java.util.List;

/// An element capable of taking actions.
public sealed interface Actor extends Element permits Player, Unit {

    /// Get the player associated with this actor. If this actor is a player,
    /// this method returns the actor itself; otherwise, it returns the actor's
    /// owner.
    Player asPlayer();

    /// Check if this actor is ready to act.
    boolean isReady();

    /// Get the abilities this actor can use.
    List<Ability> getAbilities();

    /// Get the abilities this actor has used this turn.
    List<Ability> getActiveAbilities();

    /// Set the abilities this actor has used this turn.
    Actor setActiveAbilities(List<Ability> abilities);

    /// Check if two elements are on the same team.
    boolean isAlly(Element other);

    /// Check if two elements are on different teams.
    boolean isEnemy(Element other);

}
