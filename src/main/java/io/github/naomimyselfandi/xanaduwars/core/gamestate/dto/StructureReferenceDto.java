package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/// A DTO representing a reference to a structure.
public record StructureReferenceDto(@JsonProperty("structureX") int x, @JsonProperty("structureY") int y)
        implements PhysicalRefDto {}
