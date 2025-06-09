package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonCommentable
@RequiredArgsConstructor
class UnitTypeImpl implements UnitType {

    @JsonProperty
    @EqualsAndHashCode.Include
    private final @NotNull @Valid UnitTypeId id;

    @JsonProperty
    private final @NotNull @Valid Name name;

    @JsonProperty
    private final @NotNull Set<@NotNull @Valid UnitTag> tags;

    @JsonProperty
    private @Positive int speed, vision;

    @JsonProperty
    @JsonDeserialize(contentAs = WeaponImpl.class)
    private @NotNull List<@NotNull @Valid Weapon> weapons = List.of();

    @JsonProperty
    @JsonDeserialize(contentAs = ActionImpl.class)
    private @NotNull List<@NotNull @Valid Action> abilities = List.of();

    @JsonProperty
    private @NotNull @Valid Hangar hangar = Hangar.EMPTY;

    @JsonProperty
    private @Positive int supplyCost;

    @JsonProperty
    private @PositiveOrZero int aetherCost;

    @Override
    public String toString() {
        return name.name();
    }

}
