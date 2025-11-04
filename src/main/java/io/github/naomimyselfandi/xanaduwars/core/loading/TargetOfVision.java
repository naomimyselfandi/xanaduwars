package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.Node;

record TargetOfVision<T extends Node>(Target<T, T> base) implements TargetRestrictor<T> {

    @Override
    public boolean validateFurther(Actor actor, T target) {
        return actor.asPlayer().perceives(target);
    }

}
