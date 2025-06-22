package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;
import org.jetbrains.annotations.Nullable;

/// An element with a physical presence in a game. All elements besides players
/// are physical.
@ExcludeFromCoverageReport // Work around Mockito coverage report bug
public sealed interface Physical extends Element permits Asset, Node {

    /// Get the tile associated with this element.
    @Nullable Tile getTile();

    /// Get the distance between two physical elements.
    default @Nullable Integer getDistance(Physical that) {
        return this.getTile() instanceof Tile thisTile
                && that.getTile() instanceof Tile thatTile
                ? thisTile.getDistance(thatTile) : null;
    }

}
