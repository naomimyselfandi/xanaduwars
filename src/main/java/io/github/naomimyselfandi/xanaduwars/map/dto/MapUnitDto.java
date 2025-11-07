package io.github.naomimyselfandi.xanaduwars.map.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

/// A DTO representing a unit on a map.
public record MapUnitDto(@NotEmpty String type, @PositiveOrZero int owner) {}
