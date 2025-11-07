package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.Nullable;

/// A DTO representing a tile in a map.
@NotCovered // Trivial
public record MapTileDto(@NotEmpty String type, @Nullable MapUnitDto unit) {}
