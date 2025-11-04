package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

record TargetOfTileTag(Target<Tile, Tile> base, @Unmodifiable List<TileTag> tags) implements TargetRestrictor<Tile> {

    @Override
    public boolean validateFurther(Actor actor, Tile target) {
        return target.getTags().stream().anyMatch(tags::contains);
    }

}
