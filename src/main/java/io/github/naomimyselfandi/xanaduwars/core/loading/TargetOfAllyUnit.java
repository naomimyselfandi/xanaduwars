package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;

record TargetOfAllyUnit(Target<Unit> base) implements TargetRestrictor<Unit> {

    @Override
    public boolean validateFurther(Actor actor, Unit target) {
        return actor.isAlly(target);
    }

}
