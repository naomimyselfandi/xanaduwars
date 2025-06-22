package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Direction;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A DTO representing a path on the map.
public record PathRefDto(@NotNull @Unmodifiable List<Direction> path) implements TargetRefDto {

    /// A DTO representing a path on the map.
    /// @implSpec This constructor takes an immutable copy of its argument.
    public PathRefDto(List<Direction> path) {
        this.path = List.copyOf(path);
    }

}
