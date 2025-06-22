package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

import java.util.Optional;

/// A query that checks whether an asset can see a tile.
public record AssetVisionQuery(Asset subject, Tile target) implements Query<Boolean> {

    @Override
    public Boolean defaultValue() {
        return Optional
                .ofNullable(subject.getTile())
                .map(tile -> tile.getDistance(target))
                .filter(distance -> distance <= subject.getVision())
                .isPresent();
    }

}
