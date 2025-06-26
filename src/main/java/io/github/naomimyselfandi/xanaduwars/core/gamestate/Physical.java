package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;

import java.util.Optional;

/// An element with a physical presence in a game. All elements besides players
/// are physical.
@ExcludeFromCoverageReport // Work around Mockito coverage report bug
public sealed interface Physical extends Element permits Asset, Node {

    /// Get the tile associated with this element.
    Optional<Tile> getTile();

    /// Get the distance between two physical elements. This always returns an
    /// integer unless either element is a unit inside another unit, in which
    /// case it returns `NaN`.
    default double getDistance(Physical that) {
        return getTile()
                .flatMap(tile -> that.getTile().map(tile::getDistance))
                .map(Integer::doubleValue)
                .orElse(Double.NaN);
    }

}
