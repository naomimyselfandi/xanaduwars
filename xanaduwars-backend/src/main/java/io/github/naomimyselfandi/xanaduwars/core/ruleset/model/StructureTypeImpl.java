package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonCommentable
@RequiredArgsConstructor
class StructureTypeImpl implements StructureType {

    @JsonProperty
    private final @NotNull @Valid StructureTypeId id;

    @JsonProperty
    private final @NotNull @Valid Name name;

    @JsonProperty
    private final @NotNull Set<@NotNull @Valid StructureTag> tags;

    @JsonProperty
    @JsonSerialize(using = ToStringSerializer.class)
    private @NotNull TileType foundation;

    @JsonProperty
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private @NotNull List<UnitType> deploymentRoster = List.of();

    @JsonProperty
    private @PositiveOrZero int supplyIncome;

    @JsonProperty
    private @PositiveOrZero int aetherIncome;

    @JsonProperty
    private @Positive int supplyCost;

    @JsonProperty
    private @PositiveOrZero int aetherCost;

    @JsonProperty
    private @Positive int buildTime;

    @JsonProperty
    private @PositiveOrZero int vision;

    @JsonProperty
    private MovementTable movementTable = MovementTable.EMPTY;

    @JsonProperty
    private double cover;

    @Override
    public String toString() {
        return name.name();
    }

}
