package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Scalar;

/// An event that causes a combat stage to happen.
/// @param subject The unit that's attacking.
/// @param target The target of the attack.
/// @param stage Which stage this is - the initial attack or the counterattack.
/// @param damage The damage to be inflicted.
/// @param actualDamage The damage, clamped to the target's HP.
public record AttackEvent(
        @Override Unit subject,
        @Override Node target,
        AttackStage stage,
        Scalar damage,
        Percent actualDamage
) implements TargetQuery.Event<Unit, Node> {

    /// Construct an attack event with an automatically calculated damage value.
    public static AttackEvent of(Unit subject, Node target, AttackStage stage) {
        var calculation = new AttackCalculation(subject, target, stage);
        var damage = subject.gameState().evaluate(calculation);
        var actualDamage = Percent.clamp(Math.min(damage.doubleValue(), target.hp().doubleValue()));
        return new AttackEvent(subject, target, stage, damage, actualDamage);
    }

}
