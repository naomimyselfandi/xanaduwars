package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

record TargetValidatorForVision() implements TargetValidator<Element, Action, Asset> {

    @Override
    public boolean test(Element actor, Action action, Asset target, TargetSpec spec) {
        return actor.getOwner() instanceof Player player && player.canSee(target);
    }

    @Override
    public Result.Fail fail() {
        return Result.fail("Lost vision of target.");
    }

}
