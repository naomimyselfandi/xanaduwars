package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.xanaduwars.core.message.ContextualRuleSource;

/// An element of a game state.
public sealed interface Element extends ContextualRuleSource permits Actor, Node, Player {

    /// Get the game state this element is part of.
    GameState getGameState();

    /// Initialize this element.
    /// @throws IllegalStateException if this element is already initialized.
    void initialize(GameState gameState);

}
