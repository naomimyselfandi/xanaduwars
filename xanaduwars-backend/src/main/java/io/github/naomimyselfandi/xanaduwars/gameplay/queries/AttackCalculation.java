package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Scalar;

/// A query that calculates an attack or counterattack's damage.
public record AttackCalculation(@Override Unit subject, @Override Node target, AttackStage stage)
        implements TargetQuery<Scalar, Unit, Node> {

    @Override
    public Scalar defaultValue() {
        return subject.damageTable().getOrDefault(target.type(), Percent.ZERO);
    }

}
