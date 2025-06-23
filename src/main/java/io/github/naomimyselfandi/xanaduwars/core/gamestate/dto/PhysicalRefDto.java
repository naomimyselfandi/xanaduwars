package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/// A DTO representing a reference to a physical element.
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(StructureReferenceDto.class),
        @JsonSubTypes.Type(TileReferenceDto.class),
        @JsonSubTypes.Type(UnitReferenceDto.class),
})
public sealed interface PhysicalRefDto extends ActorRefDto, TargetRefDto
        permits StructureReferenceDto, TileReferenceDto, UnitReferenceDto {

    /// Get the referent's X-coordinate.
    int x();

    /// Get the referent's Y-coordinate.
    int y();

}
