package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Physical;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

import java.util.Comparator;
import java.util.stream.Stream;

/// A query that checks whether a player can see an element.
public record VisionCheckQuery(Player subject, Physical target) implements Query<Boolean> {

    @Override
    public Boolean defaultValue() {
        return switch (target) {
            case Asset asset -> subject.isAlly(asset) || (target.getTile() instanceof Tile tile && defaultValue(tile));
            case Tile tile -> defaultValue(tile);
        };
    }

    private boolean defaultValue(Tile target) {
        var game = target.getGameState();
        var closestFirst = closestFirst(target);
        return Stream
                .concat(game.getUnits().values().stream().filter(subject::isAlly).sorted(closestFirst),
                        game.getStructures().values().stream().filter(subject::isAlly).sorted(closestFirst))
                .map(asset -> new AssetVisionQuery(asset, target))
                .anyMatch(game::evaluate);
    }

    private static Comparator<Asset> closestFirst(Tile target) {
        return Comparator
                .comparing(Asset::getTile, Comparator.nullsLast(Comparator.comparing(target::getDistance)))
                .thenComparing(Comparator.comparing(Asset::getVision).reversed());
    }

}
