package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ability;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ActionWithNodeTarget;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ActionWithFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An ability that lets a unit repair other units.
public record RepairAbility(
        @Override @NotNull @Valid Name name,
        @Override @NotNull @Valid BiFilter<Unit, Node> filter,
        @NotNull Percent baseAmount
) implements Ability.Simple<Node>, ActionWithNodeTarget<Unit>, ActionWithFilter<Unit, Node> {

    @Override
    public Execution execute(Unit subject, Node target) {
        target.hp(Percent.clamp(target.hp().doubleValue() + amount(subject)));
        return Execution.SUCCESSFUL;
    }

    @Override
    public int cost(Resource resource, Unit subject, Node target) {
        var baseCost = target.type().costs().getOrDefault(resource, 0);
        var amount = Math.min(amount(subject), 1.0 - target.hp().doubleValue());
        return (int) Math.round(baseCost * amount);
    }

    private double amount(Unit subject) {
        return baseAmount.doubleValue() * subject.hp().doubleValue();
    }

}
