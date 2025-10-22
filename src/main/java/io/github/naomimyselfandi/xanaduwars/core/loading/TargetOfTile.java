package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Stream;

enum TargetOfTile implements Target<Tile> {

    TILE;

    @Override
    public @Nullable Tile unpack(Actor actor, JsonNode target) {
        return target.get("x") instanceof IntNode x
                && target.get("y") instanceof IntNode y
                ? actor.getGameState().getTile(x.asInt(), y.asInt()) : null;
    }

    @Override
    public boolean validate(Actor actor, Object target) {
        return true;
    }

    @Override
    public Stream<Tile> propose(Actor actor) {
        return actor.getGameState().getTiles().stream();
    }

    @Override
    public JsonNode pack(Object proposal) {
        var tile = (Tile) proposal;
        return new ObjectNode(JsonNodeFactory.instance, Map.of(
                "x", new IntNode(tile.getX()),
                "y", new IntNode(tile.getY())
        ));
    }

}
