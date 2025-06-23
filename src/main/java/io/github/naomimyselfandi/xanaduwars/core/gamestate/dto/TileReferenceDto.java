package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/// A DTO representing a reference to a tile.
public record TileReferenceDto(@JsonProperty("tileX") int x, @JsonProperty("tileY") int y)
        implements PhysicalRefDto {}
