package io.github.naomimyselfandi.xanaduwars.core.actions.ability;

import io.github.naomimyselfandi.xanaduwars.core.Execution;
import io.github.naomimyselfandi.xanaduwars.core.Node;
import io.github.naomimyselfandi.xanaduwars.core.Resource;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.actions.ActionWithNodeTarget;
import io.github.naomimyselfandi.xanaduwars.core.actions.ActionWithFilter;
import io.github.naomimyselfandi.xanaduwars.core.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An ability that lets a unit repair other units.
public record RepairAbility(
        @Override @NotNull @Valid Name name,
        @Override @NotNull TagSet tags,
        @Override @NotNull @Valid BiFilter<Unit, Node> filter,
        @NotNull Percent baseAmount
) implements Ability.Simple<Node>, ActionWithNodeTarget<Unit>, ActionWithFilter<Unit, Node> {

    @Override
    public Execution execute(Unit user, Node target) {
        target.hp(Percent.clamp(target.hp().doubleValue() + baseAmount.doubleValue()));
        return Execution.SUCCESSFUL;
    }

    @Override
    public int cost(Resource resource, Unit user, Node target) {
        var baseCost = target.type().costs().getOrDefault(resource, 0);
        var amount = baseAmount.doubleValue();
        var currentHp = target.hp().doubleValue();
        if (amount + currentHp > 1.0) {
            amount = 1.0 - currentHp;
        }
        return (int) Math.round(baseCost * amount);
    }

}
