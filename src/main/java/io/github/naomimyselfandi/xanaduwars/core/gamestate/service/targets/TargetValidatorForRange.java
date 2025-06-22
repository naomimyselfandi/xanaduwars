package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Physical;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

import java.util.Optional;

record TargetValidatorForRange() implements TargetValidator<Physical, Action, Physical> {

    @Override
    public boolean test(Physical actor, Action action, Physical target, TargetSpec spec) {
        return Optional
                .ofNullable(actor.getDistance(target))
                .filter(distance -> distance >= spec.minRange())
                .filter(distance -> distance <= spec.maxRange())
                .isPresent();
    }

    @Override
    public Result.Fail fail() {
        return Result.fail("Target is out of range.");
    }

}
