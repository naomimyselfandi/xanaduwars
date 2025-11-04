package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.naomimyselfandi.xanaduwars.core.model.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/// A mixin for target specifiers that target paths.
interface TargetOfPath extends Target<List<Tile>, Tile> {

    double getCapacity(Unit unit);

    double getCapacityUsed(Unit unit, List<Tile> path);

    boolean doCustomValidation(Unit unit, List<Tile> path);

    @Override
    default List<Tile> unpack(Actor actor, JsonNode target) throws CommandException {
        var directions = unpackDirections(target);
        var size = directions.size();
        var tile = getOrigin(actor);
        var result = new ArrayList<Tile>(size);
        for (var i = 0; i < size; i++) {
            tile = tile.step(directions.get(i));
            if (tile == null) {
                throw new CommandException("Path crosses map boundary.");
            } else {
                result.add(tile);
            }
        }
        return result;
    }

    @Override
    default boolean validate(Actor actor, Object target) {
        var unit = (Unit) actor;
        @SuppressWarnings("unchecked")
        var path = (List<Tile>) target;
        path.forEach(Tile.class::cast);
        return !(getCapacityUsed(unit, path) > getCapacity(unit)) && doCustomValidation(unit, path);
    }

    @Override
    default Stream<Tile> propose(Actor actor) {
        if (actor instanceof Unit unit && unit.getLocation() instanceof Tile tile) {
            var result = new TreeSet<>(Comparator.comparing(Tile::getY).thenComparing(Tile::getX));
            new Consumer<Tile>() { // recursive helper

                private final double capacity = getCapacity(unit);
                private final List<Tile> state = new ArrayList<>();
                private final Map<Tile, Double> cache = new HashMap<>();

                @Override
                public void accept(Tile tile) {
                    var used = getCapacityUsed(unit, state);
                    if (used <= capacity) {
                        // If the unit can stop on this tile, propose it.
                        if (doCustomValidation(unit, state)) {
                            result.add(tile);
                        }
                        // If we haven't visited this tile before OR this path
                        // is cheaper than the path we used the last time we
                        // visited it, keep exploring.
                        if (used < cache.getOrDefault(tile, Double.POSITIVE_INFINITY)) {
                            cache.put(tile, used);
                            for (var direction : Direction.values()) {
                                var step = tile.step(direction);
                                if (step != null) {
                                    state.add(step);
                                    accept(step);
                                    state.removeLast();
                                }
                            }
                        }
                    }
                }

            }.accept(tile);
            return result.stream();
        } else {
            return Stream.empty();
        }
    }

    @Override
    default JsonNode pack(Tile proposal) {
        return TargetOfTile.TILE.pack(proposal);
    }

    private static List<Direction> unpackDirections(JsonNode target) throws CommandException {
        if (!target.isArray()) {
            throw new CommandException("Expected an array of directions.");
        } else try {
            return target.valueStream().map(JsonNode::asText).map(Direction::valueOf).toList();
        } catch (IllegalArgumentException e) {
            throw new CommandException("Unknown direction in path.");
        }
    }

    private static Tile getOrigin(Actor actor) throws CommandException {
        if (actor instanceof Unit unit && unit.getLocation() instanceof Tile origin) {
            return origin;
        } else {
            throw new CommandException("Not on a tile.");
        }
    }

}
