package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.PlayerId;
import org.jetbrains.annotations.Nullable;

/// Low-level data about the state of a node.
public interface NodeData {

    /// The index of the node's owner.
    @Nullable PlayerId owner();

    /// The index of the node's owner.
    NodeData owner(@Nullable PlayerId owner);

    /// The node's current HP.
    Percent hitpoints();

    /// The node's current HP.
    NodeData hitpoints(Percent hitpoints);

}
