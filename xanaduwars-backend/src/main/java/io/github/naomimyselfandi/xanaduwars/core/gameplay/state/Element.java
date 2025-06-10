package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Tag;

import java.util.Set;

/// An element of a game state.
public sealed interface Element permits Actor, Physical {

    /// The game state this element is part of.
    GameState getGameState();

    /// Any tags that apply to this element.
    Set<? extends Tag> getTags();

}
