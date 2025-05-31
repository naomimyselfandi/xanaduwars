package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An action that causes a unit to attack.
public record AttackAction(
        @Override @Valid @NotNull Name name,
        @Override @NotNull BiFilter<Unit, Node> filter
) implements SimpleActionMixin<Unit, Node>, ActionWithNodeTarget<Unit>, ActionWithFilter<Unit, Node> {

    @Override
    public Execution execute(Unit attacker, Node target) {
        attacker.gameState().evaluate(AttackEvent.of(attacker, target, AttackStage.MAIN));
        return Execution.SUCCESSFUL;
    }

}
