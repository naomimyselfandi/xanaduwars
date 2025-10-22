package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.TextNode;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.util.JsonImmutableList;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class AbstractPathAbility extends AbstractSpecification implements Ability {

    private static final Map<JsonNode, Direction> DIRECTIONS = Arrays
            .stream(Direction.values())
            .collect(Collectors.toMap(it -> new TextNode(it.name()), Function.identity()));

    @JsonImmutableList
    @Setter(AccessLevel.PACKAGE)
    @Getter(onMethod_ = @Override)
    private @NotNull List<AbilityTag> tags = List.of();

    @Override
    public Object unpack(Actor actor, JsonNode target) throws CommandException {
        var directions = unpackDirections(target);
        var tile = getOrigin(actor);
        var result = new Tile[directions.size()];
        for (var i = 0; i < result.length; i++) {
            tile = tile.step(directions.get(i));
            if (tile == null) {
                throw new CommandException("Path crosses map boundary.");
            } else {
                result[i] = tile;
            }
        }
        return result;
    }

    private static List<Direction> unpackDirections(JsonNode target) throws CommandException {
        if (target.getNodeType() == JsonNodeType.ARRAY && target.valueStream().allMatch(DIRECTIONS::containsKey)) {
            return target.valueStream().map(DIRECTIONS::get).toList();
        } else {
            throw new CommandException("Malformed path '%s'.".formatted(target));
        }
    }

    private static Tile getOrigin(Actor actor) throws CommandException {
        if (actor instanceof Unit unit && unit.getLocation() instanceof Tile origin) {
            return origin;
        } else {
            throw new CommandException("Only units on tiles can move.");
        }
    }

    @Override
    public void validate(Actor actor, Object target) throws CommandException {
        var unit = (Unit) actor;
        var path = List.of((Tile[]) target);
        if (getCapacityUsed(unit, path) > getCapacity(unit) || !validate(unit, path)) {
            throw new CommandException("Invalid path.");
        }
    }

    @Override
    public Stream<Object> propose(Actor actor) {
        return new Accumulator(actor).start().map(TargetOfTile.TILE::pack);
    }

    abstract double getCapacity(Unit unit);

    abstract double getCapacityUsed(Unit unit, List<Tile> path);

    abstract boolean validate(Unit unit, List<Tile> path);

    private class Accumulator {

        private final List<Tile> path = new ArrayList<>();

        private final Set<Tile> result = new TreeSet<>(Comparator.comparing(Tile::getY).thenComparing(Tile::getX));

        private final Map<Tile, Double> cache = new HashMap<>();

        private final Unit unit;
        private final double limit;

        Accumulator(Actor actor) {
            this.unit = (Unit) actor;
            this.limit = Math.nextUp(getCapacity(unit)); // limit plus epsilon
        }

        Stream<Tile> start() {
            accumulate((Tile) unit.getLocation());
            return result.stream();
        }

        private void accumulate(Tile tile) {
            var cost = getCapacityUsed(unit, path);
            if (cost < limit) { // "limit" is actual limit plus epsilon
                if (validate(unit, path)) {
                    result.add(tile);
                }
                if (cost < cache.getOrDefault(tile, limit)) {
                    cache.put(tile, cost);
                    for (var direction : Direction.values()) {
                        var step = tile.step(direction);
                        if (step != null) {
                            path.add(step);
                            accumulate(step);
                            path.removeLast();
                        }
                    }
                }
            }
        }

    }

}
