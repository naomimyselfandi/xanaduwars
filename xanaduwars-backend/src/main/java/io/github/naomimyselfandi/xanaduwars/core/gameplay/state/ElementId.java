package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/// The ID of an element.
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(PlayerId.class),
        @JsonSubTypes.Type(StructureId.class),
        @JsonSubTypes.Type(TileId.class),
        @JsonSubTypes.Type(UnitId.class),
})
public sealed interface ElementId extends Serializable permits ActorId, NodeId {}
