package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.Node;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;

record TargetOfRange<T extends Node>(Target<T> base, int min, int max) implements TargetRestrictor<T> {

    @Override
    public boolean validateFurther(Actor actor, T target) {
        var distance = ((Unit) actor).getDistance(target);
        return distance >= min && distance <= max;
    }

}
