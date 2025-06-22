package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that calculates an asset's vision range.
public record VisionRangeQuery(Asset subject) implements Query<Integer> {

    @Override
    public Integer defaultValue() {
        return subject.getType().getVision();
    }

}
