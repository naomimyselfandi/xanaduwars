package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

import java.util.List;
import java.util.stream.Stream;

/// An action that targets a tile.
@ConvenienceMixin
public interface ActionWithTileTarget<S extends Element> extends SimpleActionMixin<S, Tile> {

    @Override
    default Stream<Tile> enumerateTargets(GameState gameState) {
        return gameState.tiles().stream().flatMap(List::stream);
    }

}
