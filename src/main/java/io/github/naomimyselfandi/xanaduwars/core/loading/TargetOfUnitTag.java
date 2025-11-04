package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitTag;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

record TargetOfUnitTag(Target<Unit, Unit> base, @Unmodifiable List<UnitTag> tags) implements TargetRestrictor<Unit> {

    @Override
    public boolean validateFurther(Actor actor, Unit target) {
        return target.getTags().stream().anyMatch(tags::contains);
    }

}
