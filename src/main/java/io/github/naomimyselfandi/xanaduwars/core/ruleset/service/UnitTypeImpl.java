package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTypeId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.FixedCost;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryName;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.util.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@JsonCommentable
@RequiredArgsConstructor
class UnitTypeImpl implements UnitType {

    private final @NotNull @Valid UnitTypeId id;

    private final @NotNull @Valid Name name;

    private final @NotNull Set<@NotNull @Valid UnitTag> tags;

    private @Positive int speed, vision;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private @NotNull List<@NotNull @Valid Action> actions = List.of();

    @JsonDeserialize(contentAs = WeaponImpl.class)
    private @NotNull List<@NotNull @Valid Weapon> weapons = List.of();

    private @NotNull @Valid FixedCost supplyCost;

    private @NotNull @Valid FixedCost aetherCost = FixedCost.ZERO;

    private @NotNull @Valid Script focusCost = FixedCost.ZERO;

    private @NotNull @Valid Script effect = Script.NULL;

    private @NotNull @Valid Script precondition = Script.NULL;

    private @NotNull @Valid Script validation = Script.NULL;

    private @NotNull List<@NotNull @Valid TargetSpec> targets = List.of();

    private @NotNull Map<@NotNull @Valid QueryName, @NotNull @Valid Script> handlers = Map.of();

    @Override
    public String toString() {
        return name.name();
    }

}
