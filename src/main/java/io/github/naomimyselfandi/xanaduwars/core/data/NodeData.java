package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import jakarta.validation.constraints.PositiveOrZero;
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
