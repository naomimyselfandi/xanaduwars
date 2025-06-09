package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonCommentable
@RequiredArgsConstructor
class TileTypeImpl implements TileType {

    @JsonProperty
    private final @NotNull @Valid TileTypeId id;

    @JsonProperty
    private final @NotNull @Valid Name name;

    @JsonProperty
    private final @NotNull Set<@NotNull @Valid TileTag> tags;

    @JsonProperty
    private MovementTable movementTable = MovementTable.EMPTY;

    @JsonProperty
    private double cover;

    @Override
    public String toString() {
        return name.name();
    }

}
