package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.ConstructionData;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.TileData;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TileDestroyedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class TileImpl extends AbstractNode<TileId, TileType, TileData> implements Tile {

    TileImpl(AugmentedGameState gameState, TileData tileData) {
        super(gameState, tileData.tileId(), tileData);
    }

    @Override
    public TileType type() {
        var index = Objects.requireNonNullElse(data.structureType(), data.tileType()).index();
        return ruleset.tileTypes().get(index);
    }

    @Override
    public Optional<TileType> foundation() {
        return Optional.ofNullable(data.structureType()).map(TileTypeId::index).map(ruleset.tileTypes()::get);
    }

    @Override
    public int distance(Tile that) {
        var thisId = this.id;
        var thatId = that.id();
        var dx = Math.abs(thisId.x() - thatId.x());
        var dy = Math.abs(thisId.y() - thatId.y());
        return dx + dy;
    }

    @Override
    public Optional<Unit> unit() {
        return gameState.units().stream().filter(unit -> equals(unit.location())).findFirst();
    }

    @Override
    public Stream<Tile> area(int radius) {
        var x = id.x();
        var y = id.y();
        return IntStream.rangeClosed(-radius, radius).boxed().flatMap(dy -> {
            var width = radius - Math.abs(dy);
            return IntStream
                    .rangeClosed(-width, width)
                    .mapToObj(dx -> gameState.tile(x + dx, y + dy))
                    .flatMap(Optional::stream);
        });
    }

    @Override
    public Optional<Tile> step(Direction direction) {
        var x = id.x();
        var y = id.y();
        return switch (direction) {
            case NORTH -> gameState.tile(x, y - 1);
            case EAST -> gameState.tile(x + 1, y);
            case SOUTH -> gameState.tile(x, y + 1);
            case WEST -> gameState.tile(x - 1, y);
        };
    }

    @Override
    public Map<Tag, Double> movementTable() {
        return type().movementTable();
    }

    @Override
    public Percent cover() {
        return type().cover();
    }

    @Override
    public Map<Resource, Integer> income() {
        return type().income();
    }

    @Override
    public Set<UnitType> deploymentRoster() {
        return type().deploymentRoster();
    }

    @Override
    public Optional<Construction> construction() {
        return Optional.ofNullable(data.construction()).map(it -> new Construction(
                ruleset.tileTypes().get(it.structureType().index()),
                it.progress()
        ));
    }

    @Override
    public Tile construction(@Nullable Construction construction) {
        var constructionData = Optional
                .ofNullable(construction)
                .map(it -> new ConstructionData().structureType(it.type().id()).progress(it.progress()))
                .orElse(null);
        data.construction(constructionData);
        return this;
    }

    @Override
    public void createStructure(TileType type, Player owner) {
        data.construction(null).hitpoints(Percent.FULL).structureType(type.id()).owner(owner.id());
    }

    @Override
    public void createUnit(UnitType type, Player owner) {
        gameState.createUnit(this, type).owner(owner);
    }

    @Override
    public Optional<Tile> tile() {
        return Optional.of(this);
    }

    @Override
    void onDestruction() {
        gameState.evaluate(new TileDestroyedEvent(this));
        data.construction(null).hitpoints(Percent.FULL).structureType(null).owner(null);
    }

}
