package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Structure;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

/// A query that calculates an asset's vision range.
public record VisionRangeQuery(Asset subject) implements Query<Integer> {

    @Override
    public Integer defaultValue() {
        return switch (subject) {
            case Structure structure -> structure.getType().getVision();
            case Unit unit -> unit.getType().getVision();
        };
    }

}
