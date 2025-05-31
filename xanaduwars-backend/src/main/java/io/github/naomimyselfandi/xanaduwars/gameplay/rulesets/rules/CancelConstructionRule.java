package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitLeftEvent;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A rule that cancels in-progress structures when the builder leaves.
public record CancelConstructionRule() implements GameRule<UnitLeftEvent, None> {

    @Override
    public boolean handles(UnitLeftEvent query, None value) {
        return query.previousLocation() instanceof Tile tile && tile.construction().isPresent();
    }

    @Override
    public None handle(UnitLeftEvent query, None value) {
        ((Tile) query.previousLocation()).construction(null);
        return None.NONE;
    }

}
