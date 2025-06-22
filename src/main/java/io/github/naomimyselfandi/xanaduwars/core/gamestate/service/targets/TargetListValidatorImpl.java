package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Physical;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

record TargetListValidatorImpl(List<TargetValidator<Element, Action, Object>> validators)
        implements TargetListValidator {

    static final TargetListValidatorImpl INSTANCE = new TargetListValidatorImpl(List.of(
            wrap(new TargetValidatorForIff(), Element.class, Action.class, Element.class),
            wrap(new TargetValidatorForRange(), Physical.class, Action.class, Physical.class),
            wrap(new TargetValidatorForVision(), Element.class, Action.class, Asset.class),
            wrap(new TargetValidatorForWeapon(), Unit.class, Weapon.class, Asset.class)
    ));

    @Override
    public Result test(Element actor, Action action, List<Object> targets) {
        var specs = action.getTargets();
        var count = Math.max(specs.size(), targets.size()); // so we throw if we got this far with bad data
        return IntStream
                .range(0, count)
                .boxed()
                .flatMap(i -> test(actor, action, targets.get(i), specs.get(i)))
                .filter(Result.Fail.class::isInstance)
                .findFirst()
                .orElse(Result.okay());
    }

    private Stream<Result> test(Element actor, Action action, Object target, TargetSpec spec) {
        return validators
                .stream()
                .filter(it -> !it.test(actor, action, target, spec))
                .map(TargetValidator::fail);
    }

    private static <A, X, T> TargetValidator<Element, Action, Object> wrap(
            TargetValidator<A, X, T> validator,
            Class<A> a, Class<X> x, Class<T> t
    ) {
        return new TargetValidatorWrapper<>(validator, a, x, t);
    }

}
