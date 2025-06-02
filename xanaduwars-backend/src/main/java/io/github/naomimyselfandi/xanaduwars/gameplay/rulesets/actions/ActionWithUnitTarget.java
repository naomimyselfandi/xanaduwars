package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.Actor;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

import java.util.stream.Stream;

/// An action that targets a unit.
@ConvenienceMixin
public interface ActionWithUnitTarget<S extends Actor> extends SimpleActionMixin<S, Unit> {

    @Override
    default Stream<Unit> enumerateTargets(GameState gameState) {
        return gameState.units().stream();
    }

}
