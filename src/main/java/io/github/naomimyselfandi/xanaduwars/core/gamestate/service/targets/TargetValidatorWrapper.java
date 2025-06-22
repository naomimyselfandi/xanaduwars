package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

record TargetValidatorWrapper<A, X, T>(TargetValidator<A, X, T> delegate, Class<A> a, Class<X> x, Class<T> t)
        implements TargetValidator<Element, Action, Object> {

    @Override
    public boolean test(Element actor, Action action, Object target, TargetSpec spec) {
        if (a.isInstance(actor) && x.isInstance(action) && t.isInstance(target)) {
            return delegate.test(a.cast(actor), x.cast(action), t.cast(target), spec);
        } else {
            return true;
        }
    }

    @Override
    public Result.Fail fail() {
        return delegate.fail();
    }

}
