package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Tile;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that checks whether an asset can see a tile.
public record VisionCheckQuery(Asset subject, Tile target) implements Query<Boolean> {

    @Override
    public Boolean defaultValue() {
        return subject.getDistance(target) <= subject.getVision();
    }

}
