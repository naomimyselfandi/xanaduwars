package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

import java.util.List;

/// A helper that evaluates the standard targeting requirements.
public interface TargetListValidator {

    /// A helper that evaluates the standard targeting requirements.
    TargetListValidator INSTANCE = TargetListValidatorImpl.INSTANCE;

    /// Evaluate the standard targeting requirements.
    Result test(Element actor, Action action, List<Object> targets);

}
