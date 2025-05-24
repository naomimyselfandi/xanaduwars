package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Tile;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A path a unit is moving along.
/// @param start The tile the path starts on.
/// @param steps The steps along the path. This is never empty, and each step is
/// adjacent to the previous step (or the starting tile, for the first step).
public record Path(Tile start, @Unmodifiable List<Tile> steps) {

    /// A path a unit is moving along.
    /// @param start The tile the path starts on.
    /// @param steps The steps along the path.
    /// @implSpec This constructor takes an immutable copy of its list argument.
    /// @throws IllegalArgumentException if no steps are given, the first step
    /// is not adjacent to the starting tile, or any consecutive pair of steps
    /// are not adjacent to each other.
    public Path(Tile start, List<Tile> steps) {
        this.start = start;
        this.steps = List.copyOf(steps);
        throwIfEmpty();
        throwIfHasNonAdjacentSteps();
    }

    private void throwIfEmpty() {
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }
    }

    private void throwIfHasNonAdjacentSteps() {
        var previous = start;
        for (var step : steps) {
            if (previous.distance(step) != 1) {
                throw new IllegalArgumentException("Path with non-adjacent tiles");
            }
            previous = step;
        }
    }

}
