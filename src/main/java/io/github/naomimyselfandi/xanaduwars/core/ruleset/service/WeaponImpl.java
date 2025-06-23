package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.FixedCost;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.util.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@JsonCommentable
class WeaponImpl implements Weapon {

    private @NotNull @Valid Name name;

    private @NotNull Set<@NotNull @Valid ActionTag> tags = Set.of();

    private @PositiveOrZero int minRange, maxRange = 1;

    private @NotEmpty Map<@NotNull DamageKey, @NotNull @Positive Integer> damage = Map.of();

    private @NotNull @Valid Script supplyCost = FixedCost.ZERO;

    private @NotNull @Valid Script aetherCost = FixedCost.ZERO;

    private @NotNull @Valid Script focusCost = FixedCost.ZERO;

    private @NotNull @Valid Script effect = Script.NULL;

    private @NotNull @Valid Script precondition = Script.NULL;

    private @NotNull @Valid Script validation = Script.NULL;

    @Getter(lazy = true)
    private final List<@Valid TargetSpec> targets = List.of(TargetSpec
            .builder()
            .filters(Map.of(Kind.STRUCTURE, true, Kind.UNIT, true, Iff.ENEMY, true, Iff.NEUTRAL, true))
            .minRange(minRange)
            .maxRange(maxRange)
            .build());

    @Override
    public String toString() {
        return name.name();
    }

}
