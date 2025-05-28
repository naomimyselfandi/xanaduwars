package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A rule that grants focus after combat.
public record AttackFocusRule(
        @Override @NotNull @Valid Filter<Unit> subjectFilter,
        @Override @NotNull @Valid BiFilter<Unit, Node> targetFilter,
        @NotNull Percent attackerMultiplier,
        @NotNull Percent defenderMultiplier,
        @NotNull Resource resource
) implements TargetRule<AttackEvent, None, Unit, Node> {

    @Override
    public None handle(AttackEvent query, None value) {
        var subject = query.subject();
        var target = query.target();
        var cost = target.type().costs().getOrDefault(resource, 0);
        var multiplier = query.actualDamage().doubleValue();
        var baseAmount = cost * multiplier;
        // The subject's owner may be missing if this is a counterattack by a neutral unit.
        subject.owner().ifPresent(player -> addFocus(baseAmount, attackerMultiplier, player));
        target.owner().ifPresent(player -> addFocus(baseAmount, defenderMultiplier, player));
        return None.NONE;
    }

    private void addFocus(double baseAmount, Percent multiplier, Player player) {
        var amount = (int) Math.round(baseAmount * multiplier.doubleValue());
        player.resource(Resource.FOCUS, player.resources().get(Resource.FOCUS) + amount);
    }

}
