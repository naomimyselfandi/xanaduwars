package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.GameState;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

import java.util.stream.Stream;

/// An action that targets a unit.
@ConvenienceMixin
public interface ActionWithUnitTarget<S extends Element> extends SimpleActionMixin<S, Unit> {

    @Override
    default Stream<Unit> enumerateTargets(GameState gameState) {
        return gameState.units().stream();
    }

}
