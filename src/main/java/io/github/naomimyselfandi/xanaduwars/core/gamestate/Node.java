package io.github.naomimyselfandi.xanaduwars.core.gamestate;

/// An element that can contain a unit. Tiles and units are both nodes.
public sealed interface Node extends Physical permits Tile, Unit {

    /// Get this element's unique ID.
    @Override NodeId getId();

}
