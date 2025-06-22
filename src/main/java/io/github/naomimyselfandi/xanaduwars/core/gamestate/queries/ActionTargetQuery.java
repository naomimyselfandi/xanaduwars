package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets.TargetListValidator;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

import java.util.List;

/// A query that validates an action's targets.
public record ActionTargetQuery(Element subject, Action action, List<Object> targets, TargetListValidator validator)
        implements QueryWithTargets<Result> {

    /// A query that validates an action's targets.
    public ActionTargetQuery(Element subject, Action action, List<Object> targets, TargetListValidator validator) {
        this.subject = subject;
        this.action = action;
        this.targets = List.copyOf(targets);
        this.validator = validator;
    }

    /// A query that validates an action's targets.
    public ActionTargetQuery(Element subject, Action action, List<Object> targets) {
        this(subject, action, targets, TargetListValidator.INSTANCE);
    }

    @Override
    public Result defaultValue() {
        return validator.test(subject, action, targets);
    }

}
