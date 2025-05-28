package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.NodeId;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/// A game state element that physically appears on the map. Units and tiles are
/// both nodes.
public sealed interface Node extends Element permits Tile, Unit {

    /// The type of node this is.
    @Override
    NodeType type();

    /// This node's ID.
    NodeId id();

    /// This node's current hit points.
    Percent hp();

    /// This node's current hit points.
    Node hp(Percent hp);

    /// Transfer ownership of this node.
    Node owner(@Nullable Player owner);

    /// View this node as a tile. If this node is a tile, this returns this
    /// tile. If this node is a unit on this tile, this returns that tile.
    /// Otherwise, this returns nothing.
    Optional<Tile> tile();

    /// The distance between this node and another node. This returns an empty
    /// result if and only if either node is a unit which is not on a tile.
    Optional<Integer> distance(Node that);

}
