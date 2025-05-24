package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Node;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Scalar;

/// An event that indicates combat damage was applied.
public record AttackEvent(@Override Unit subject, @Override Node target, AttackStage stage, Scalar damage)
        implements TargetQuery.Event {}
