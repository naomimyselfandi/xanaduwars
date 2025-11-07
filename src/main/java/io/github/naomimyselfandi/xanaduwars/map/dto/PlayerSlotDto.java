package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.validation.constraints.PositiveOrZero;

/// A DT representing a map's player slot.
@NotCovered // Trivial
public record PlayerSlotDto(@PositiveOrZero int team) {}
