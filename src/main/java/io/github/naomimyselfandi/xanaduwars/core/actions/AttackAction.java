package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.core.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.core.queries.AttackStage;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An action that causes a unit to attack.
public record AttackAction(
        @Override @Valid @NotNull Name name,
        @Override @NotNull TagSet tags
) implements SimpleActionMixin<Unit, Node>, ActionWithNodeTarget<Unit> {

    @Override
    public Execution execute(Unit attacker, Node target) {
        var gameState = attacker.gameState();
        var baseDamage = attacker.damageTable().get(target.type());
        var calculation = new AttackCalculation(attacker, target, AttackStage.MAIN);
        var damage = gameState.evaluate(calculation, baseDamage);
        target.hp(Percent.clamp(target.hp().doubleValue() - damage.doubleValue()));
        gameState.evaluate(new AttackEvent(attacker, target, AttackStage.MAIN, damage));
        return Execution.SUCCESSFUL;
    }

}
