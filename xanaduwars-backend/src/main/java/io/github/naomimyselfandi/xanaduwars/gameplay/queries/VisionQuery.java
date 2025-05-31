package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;

/// A query that checks whether a player can see a node. Since a player can
/// always see allied nodes, a vision query automatically short-circuits to
/// `true` when the player and the node are allied; rules do not need to go
/// out of their way to preserve this invariant.
public record VisionQuery(@Override Player subject, @Override Node target)
        implements TargetQuery.Validation<Player, Node> {

    @Override
    public boolean shouldShortCircuit(Boolean value) {
        return TargetQuery.Validation.super.shouldShortCircuit(value) || subject.isAlly(target);
    }

}
