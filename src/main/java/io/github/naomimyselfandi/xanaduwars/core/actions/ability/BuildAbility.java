package io.github.naomimyselfandi.xanaduwars.core.actions.ability;

import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.stream.Stream;

/// An ability that allows a unit to build structures.
public record BuildAbility(
        @Override @NotNull @Valid Name name,
        @Override @NotNull TagSet tags
) implements Ability.Simple<TileType> {

    @Override
    public Stream<TileType> enumerateTargets(GameState gameState) {
        return gameState.ruleset().tileTypes().stream();
    }

    @Override
    public int cost(Resource resource, Unit builder, TileType tileType) {
        if (builder.tile().flatMap(Tile::construction).isEmpty()) {
            return tileType.costs().getOrDefault(resource, 0);
        } else {
            return 0;
        }
    }

    @Override
    public Execution execute(Unit builder, TileType tileType) {
        var tile = builder.tile().orElseThrow();
        var progress = currentProgress(tile) + deltaProgress(tileType, builder);
        if (progress >= 1.0) {
            tile.createStructure(tileType, builder.owner().orElseThrow());
        } else {
            tile.construction(new Construction(tileType, new Percent(progress)));
        }
        return Execution.SUCCESSFUL;
    }

    private static double currentProgress(Tile tile) {
        return tile.construction().map(Construction::progress).map(Percent::doubleValue).orElse(0.0);
    }

    private static double deltaProgress(TileType tileType, Unit builder) {
        return builder.hp().doubleValue() / tileType.buildTime();
    }

}
