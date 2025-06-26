package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Physical;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

record TargetValidatorForRange() implements TargetValidator<Physical, Action, Physical> {

    @Override
    public boolean test(Physical actor, Action action, Physical target, TargetSpec spec) {
        var distance = actor.getDistance(target);
        return distance >= spec.minRange() && distance <= spec.maxRange();
    }

    @Override
    public Result.Fail fail() {
        return Result.fail("Target is out of range.");
    }

}
