package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.common.Iff;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

record TargetValidatorForIff() implements TargetValidator<Element, Action, Element> {

    @Override
    public Result.Fail fail() {
        return Result.fail("Target is on wrong team.");
    }

    @Override
    public boolean test(Element actor, Action action, Element target, TargetSpec spec) {
        return spec.filters().getOrDefault(getIff(actor, target), false);
    }

    private static Iff getIff(Element actor, Element target) {
        if (actor.hasSameOwner(target)) {
            return Iff.OWN;
        } else if (actor.isAlly(target)) {
            return Iff.ALLY;
        } else if (actor.isEnemy(target)) {
            return Iff.ENEMY;
        } else {
            return Iff.NEUTRAL;
        }
    }

}
