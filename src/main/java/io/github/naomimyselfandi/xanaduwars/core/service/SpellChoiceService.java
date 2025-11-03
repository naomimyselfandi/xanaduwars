package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.model.*;

import java.util.List;

/// A service that manages pre-game spell choices.
public interface SpellChoiceService {

    /// Record a player's chosen commander and spells.
    void select(Player player, Commander commander, List<Ability> abilities) throws SpellChoiceException;

    /// Suggest spells a player could pick. This method allows a spell list to
    /// be built up incrementally, starting from an empty list.
    /// @param gameState The game state the player is part of.
    /// @param commander The commander the player intends to choose.
    /// @param abilities Any spells the player intends to choose.
    List<Ability> getSuggestions(GameState gameState, Commander commander, List<Ability> abilities);

}
