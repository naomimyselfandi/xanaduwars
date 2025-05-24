package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.data.NodeData;
import io.github.naomimyselfandi.xanaduwars.core.queries.NodeDestroyedEvent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

abstract class AbstractNode<I, T extends NodeType, N extends NodeData> extends AbstractElement<I, T> {

    // We can't extend Node because it's sealed.

    final N data;

    AbstractNode(AugmentedGameState gameState, I id, N data) {
        super(gameState, id);
        this.data = data;
    }

    public Percent hp() {
        return data.hitpoints();
    }

    public Node hp(Percent hp) {
        var self = (Node) this;
        if (hp.equals(Percent.ZERO)) {
            gameState.evaluate(new NodeDestroyedEvent(self));
            onDestruction();
        } else {
            data.hitpoints(hp);
        }
        return self;
    }

    abstract void onDestruction();

    public Optional<Player> owner() {
        return Optional.ofNullable(data.owner()).map(id -> gameState.players().get(id.intValue()));
    }

    public Node owner(@Nullable Player owner) {
        data.owner(owner == null ? null : owner.id());
        return (Node) this;
    }

    public abstract Optional<Tile> tile();

    public Optional<Integer> distance(Node that) {
        return tile().flatMap(self -> that.tile().map(self::distance));
    }

}
