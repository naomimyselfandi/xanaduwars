package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;
import io.github.naomimyselfandi.xanaduwars.ext.None;

import java.util.stream.Stream;

/// An action that targets nothing.
@ConvenienceMixin
public interface ActionWithoutTarget<S extends Element> extends SimpleActionMixin<S, None> {

    @Override
    default Stream<None> enumerateTargets(GameState gameState) {
        return Stream.of(None.NONE);
    }

}
