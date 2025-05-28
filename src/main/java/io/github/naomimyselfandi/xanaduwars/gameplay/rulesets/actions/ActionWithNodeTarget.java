package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

import java.util.List;
import java.util.stream.Stream;

/// An action that targets a node.
@ConvenienceMixin
public interface ActionWithNodeTarget<S extends Element> extends SimpleActionMixin<S, Node> {

    @Override
    default Stream<Node> enumerateTargets(GameState gameState) {
        return Stream.concat(gameState.units().stream(), gameState.tiles().stream().flatMap(List::stream));
    }

}
