package io.github.naomimyselfandi.xanaduwars.core.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.queries.Path;
import io.github.naomimyselfandi.xanaduwars.core.queries.PathValidation;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/// An action that causes a unit to move.
@Data
public final class MoveAction implements SimpleActionMixin<Unit, List<Direction>> {

    @JsonProperty
    private @NotNull @Valid Name name;

    @JsonProperty
    private @NotNull TagSet tags;

    @ToString.Exclude
    private MoveLogic moveLogic = MoveLogicImpl.MOVE_LOGIC;

    @Override
    public Stream<List<Direction>> enumerateTargets(GameState gameState) {
        // This action has special permission to skip target enumeration.
        return Stream.empty();
    }

    @Override
    public boolean test(Unit unit, List<Direction> directions) {
        return path(unit, directions)
                .map(path -> unit.gameState().evaluate(new PathValidation(unit, path)))
                .orElse(false);
    }

    @Override
    public Execution execute(Unit unit, List<Direction> directions) {
        // path() returns an empty Optional if we tried to move off the map.
        // Since test() checks for this and the map dimensions aren't hidden
        // information, we can assume we have a valid path.
        var path = path(unit, directions).orElseThrow();
        return moveLogic.execute(path, unit);
    }

    private static Optional<Path> path(Unit unit, List<Direction> directions) {
        return unit.tile().map(start -> path(start, directions));
    }

    private static @Nullable Path path(Tile start, List<Direction> directions) {
        if (directions.isEmpty()) return null; // Empty paths aren't allowed.
        var steps = new ArrayList<Tile>(directions.size());
        var tile = start;
        for (var direction : directions) {
            if (tile.step(direction).orElse(null) instanceof Tile step) {
                steps.add(step);
                tile = step;
            } else {
                // If we got here, the path would fall off the edge of the map.
                return null;
            }
        }
        return new Path(start, steps);
    }

}
