package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ability;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.stream.Stream;

/// An ability that allows a unit to build structures.
public record BuildAbility(
        @Override @NotNull @Valid Name name,
        @Override @NotEmpty Set<@Structure TileType> tileTypes
) implements Ability.Simple<TileType> {

    /// An ability that allows a unit to build structures.
    /// @implSpec This constructor takes an immutable copy of its set argument.
    public BuildAbility(Name name, Set<TileType> tileTypes) {
        this.name = name;
        this.tileTypes = Set.copyOf(tileTypes);
    }

    @Override
    public Stream<TileType> enumerateTargets(GameState gameState) {
        return tileTypes.stream();
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
    public boolean test(Unit user, TileType tileType) {
        return tileTypes.contains(tileType) && user.tile().map(Tile::type).equals(tileType.foundation());
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
