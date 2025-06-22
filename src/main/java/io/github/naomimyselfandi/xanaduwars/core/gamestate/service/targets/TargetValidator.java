package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

interface TargetValidator<A, X, T> {

    boolean test(A actor, X action, T target, TargetSpec spec);

    Result.Fail fail();

}
