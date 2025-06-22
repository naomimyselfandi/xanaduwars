package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.common.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

import java.util.Set;

/// A query that calculates a tile's tag set.
public record TileTagQuery(Tile subject) implements Query<Set<TileTag>> {

    @Override
    public Set<TileTag> defaultValue() {
        return subject.getType().getTags();
    }

}
