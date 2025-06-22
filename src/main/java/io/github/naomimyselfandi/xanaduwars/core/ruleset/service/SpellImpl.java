package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellTag;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.FixedCost;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.core.scripting.*;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.util.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
class SpellImpl implements Spell {

    private final @NotNull @Valid SpellId id;

    private final @NotNull @Valid Name name;

    private final @NotNull Set<@NotNull @Valid SpellTag> tags;

    private @NotNull List<@NotNull TargetSpec> targets = List.of();

    private @NotNull @Valid Script precondition = Script.NULL;

    private @NotNull @Valid Script validation = Script.NULL;

    private @NotNull @Valid Script effect = Script.NULL;

    private @NotNull @Valid Script supplyCost = FixedCost.ZERO;

    private @NotNull @Valid Script aetherCost = FixedCost.ZERO;

    private @NotNull @Valid FixedCost focusCost = FixedCost.ZERO;

    private boolean signatureSpell;

    private @NotNull Map<@NotNull @Valid QueryName, @NotNull @Valid Script> handlers = Map.of();

    @Override
    public String toString() {
        return name.name();
    }

}
