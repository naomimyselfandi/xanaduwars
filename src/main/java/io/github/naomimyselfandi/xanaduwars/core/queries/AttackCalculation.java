package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Node;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Scalar;

/// A query that calculates an attack or counterattack's damage.
public record AttackCalculation(@Override Unit subject, @Override Node target, AttackStage stage)
        implements TargetQuery<Scalar> {}
