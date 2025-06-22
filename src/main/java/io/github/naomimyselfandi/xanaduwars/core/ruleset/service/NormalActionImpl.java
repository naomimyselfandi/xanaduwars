package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.common.ActionTag;
import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.FixedCost;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.util.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonCommentable
class NormalActionImpl implements NormalAction {

    private final @NotNull @Valid Name name;

    private final @NotNull Set<@NotNull @Valid ActionTag> tags;

    private @NotNull List<@NotNull TargetSpec> targets = List.of();

    private @NotNull @Valid Script precondition = Script.NULL;

    private @NotNull @Valid Script validation = Script.NULL;

    private @NotNull @Valid Script effect = Script.NULL;

    private @NotNull @Valid Script supplyCost = FixedCost.ZERO;

    private @NotNull @Valid Script aetherCost = FixedCost.ZERO;

    private @NotNull @Valid Script focusCost = FixedCost.ZERO;

    @Override
    public String toString() {
        return name.name();
    }

}
