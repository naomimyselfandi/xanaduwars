package io.github.naomimyselfandi.xanaduwars.core.model;

import java.util.List;
import java.util.stream.Stream;

/// A player in a game.
public non-sealed interface Player extends Element, Actor {

    /// Get this player's position in the turn order.
    int getPosition();

    /// Get this player's team. In a non-team game, the player's position in the
    /// turn order is used as a placeholder.
    int getTeam();

    /// Set this player's team. This is typically called during game setup, not
    /// during gameplay.
    Player setTeam(int team);

    /// Get the commander this player is playing as.
    Commander getCommander();

    /// Set the commander this player is playing as. This is typically called
    /// during game setup, not during gameplay.
    Player setCommander(Commander commander);

    /// Set the abilities this player has access to. This is typically called
    /// during game setup to record a player's spell choices.
    Player setAbilities(List<Ability> abilities);

    /// Get the abilities this player has cast this game.
    List<Ability> getUsedAbilities();

    /// Set the abilities this player has used this game.
    Player setUsedAbilities(List<Ability> abilities);

    /// Check if this player has been defeated.
    boolean isDefeated();

    /// Set whether this player has been defeated.
    Player setDefeated(boolean defeated);

    /// Get this player's current supplies. Supplies are one of the in-game
    /// resources, typically used to create units.
    int getSupplies();

    /// Set this player's current supplies. Supplies are one of the in-game
    /// resources, typically used to create units.
    Player setSupplies(int supplies);

    /// Get this player's current aether. Aether is one of the in-game
    /// resources, typically used to create advanced units.
    int getAether();

    /// Set this player's current aether. Aether is one of the in-game
    /// resources, typically used to create advanced units.
    Player setAether(int aether);

    /// Get this player's current focus. Focus is one of the in-game resources,
    /// typically used to cast spells.
    int getFocus();

    /// Set this player's current focus. Focus is one of the in-game resources,
    /// typically used to cast spells.
    Player setFocus(int focus);

    /// Get this player's units in location order.
    Stream<Unit> getUnits();

    /// Check if this player perceives a node.
    boolean perceives(Node node);

}
