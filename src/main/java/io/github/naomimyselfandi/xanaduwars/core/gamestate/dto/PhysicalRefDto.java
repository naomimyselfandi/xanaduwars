package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/// A DTO representing a reference to a physical element.
/// @param kind The kind of element being referred to.
/// @param x The element's X-coordinate.
/// @param y The element's Y-coordinate.
public record PhysicalRefDto(Kind kind, int x, int y) implements ActorRefDto, TargetRefDto {

    /// A kind of physical element.
    public enum Kind {

        @JsonProperty("Structure") STRUCTURE,

        @JsonProperty("Tile") TILE,

        @JsonProperty("Unit") UNIT

    }

}
