package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.TagSet;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Tagged;
import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

import java.util.Optional;

/// An element of a game state.
@ExcludeFromCoverageReport
public sealed interface Element extends Tagged permits Node, Player, Spell {

    /// The game state this element belongs to.
    GameState gameState();

    /// The type this element is an instance of.
    Type type();

    /// Any tags that apply to this element.
    TagSet tags();

    /// The player that owns this element.
    Optional<Player> owner();

    /// Check if this element is on the same team as another element.
    boolean isAlly(Element that);

}
