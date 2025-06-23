package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/// A DTO representing a reference to a unit.
public record UnitReferenceDto(@JsonProperty("unitX") int x, @JsonProperty("unitY") int y)
        implements PhysicalRefDto {}
