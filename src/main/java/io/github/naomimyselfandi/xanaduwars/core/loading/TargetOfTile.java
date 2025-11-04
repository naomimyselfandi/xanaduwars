package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;

import java.util.Map;
import java.util.stream.Stream;

enum TargetOfTile implements Target<Tile, Tile> {

    TILE;

    @Override
    public Tile unpack(Actor actor, JsonNode target) throws CommandException {
        if (target.size() != 2 || !(target.get("x") instanceof IntNode x) || !(target.get("y") instanceof IntNode y)) {
            throw new CommandException("Expected an object with two int values, x and y.");
        } else if (!(actor.getGameState().getTile(x.asInt(), y.asInt()) instanceof Tile tile)) {
            throw new CommandException("Target is out of bounds.");
        } else {
            return tile;
        }
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
    public JsonNode pack(Tile proposal) {
        var tile = (Tile) proposal;
        return new ObjectNode(JsonNodeFactory.instance, Map.of(
                "x", new IntNode(tile.getX()),
                "y", new IntNode(tile.getY())
        ));
    }

}
