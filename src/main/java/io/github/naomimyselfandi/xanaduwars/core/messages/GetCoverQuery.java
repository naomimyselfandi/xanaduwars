package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

/// A query that gets the cover provided by a tile. Game rules use cover in
/// damage calculations at their discretion.
public record GetCoverQuery(Tile tile) implements SimpleQuery<Double> {

    @Override
    public Double defaultValue(ScriptRuntime runtime) {
        return tile.getType().getCover();
    }

}
