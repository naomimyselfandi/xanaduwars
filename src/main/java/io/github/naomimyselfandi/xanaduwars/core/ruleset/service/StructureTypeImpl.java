package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.FixedCost;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryName;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.util.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@JsonCommentable
@RequiredArgsConstructor
class StructureTypeImpl implements StructureType {

    private final @NotNull @Valid StructureTypeId id;

    private final @NotNull @Valid Name name;

    private final @NotNull Set<@NotNull @Valid StructureTag> tags;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private @NotNull List<Action> actions = List.of();

    private @NotNull @Valid FixedCost supplyCost;

    private @NotNull @Valid FixedCost aetherCost = FixedCost.ZERO;

    private @NotNull @Valid Script focusCost = FixedCost.ZERO;

    private @NotNull @Valid Script effect = Script.NULL;

    private @NotNull @Valid Script precondition = Script.NULL;

    private @NotNull @Valid Script validation = Script.NULL;

    private @NotNull List<@NotNull @Valid TargetSpec> targets = List.of();

    private @Positive int buildTime;

    private @PositiveOrZero int vision;

    private @NotNull Map<@NotNull @Valid UnitTag, @PositiveOrZero Double> movementTable = Map.of();

    private @PositiveOrZero double cover;

    private @NotNull Map<@NotNull @Valid QueryName, @NotNull @Valid Script> handlers = Map.of();

    @Override
    public String toString() {
        return name.name();
    }

}
