package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.common.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.TileData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.TileTagQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.TerrainType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.TileType;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

record TileImpl(TileData data, @Getter GameState gameState, @Getter TileId id) implements Tile {

    @Override
    public TileType getType() {
        return gameState.getRuleset().getTileType(data.getTypeId());
    }

    @Override
    public Tile setType(TileType type) {
        data.setTypeId(type.getId());
        gameState.invalidateCache();
        return this;
    }

    @Override
    public @Unmodifiable Set<TileTag> getTags() {
        return gameState.evaluate(new TileTagQuery(this));
    }

    @Override
    public @Nullable Structure getStructure() {
        return gameState.getStructures().get(id.structureId());
    }

    @Override
    public @Nullable Unit getUnit() {
        return gameState.getUnit(this);
    }

    @Override
    public double getMovementCost(Unit unit) {
        return unit
                .getTags()
                .stream()
                .map(getEffectiveType().getMovementTable()::get)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(Double.NaN);
    }

    @Override
    public double getCover() {
        return getEffectiveType().getCover();
    }

    @Override
    public int getDistance(Tile that) {
        var thatId = that.getId();
        var dx = Math.abs(id.x() - thatId.x());
        var dy = Math.abs(id.y() - thatId.y());
        return dx + dy;
    }

    @Override
    public @Nullable Tile step(Direction direction) {
        return gameState.getTiles().get(switch (direction) {
            case NORTH -> new TileId(id.x(), id.y() - 1);
            case EAST -> new TileId(id.x() + 1, id.y());
            case SOUTH -> new TileId(id.x(), id.y() + 1);
            case WEST -> new TileId(id.x() - 1, id.y());
        });
    }

    @Override
    public Stream<Tile> getArea(int radius) {
        return IntStream
                .rangeClosed(-radius, radius)
                .boxed()
                .flatMap(dy -> {
                    var width = radius - Math.abs(dy);
                    return IntStream
                            .rangeClosed(-width, width)
                            .mapToObj(dx -> new TileId(id.x() + dx, id.y() + dy));
                })
                .map(gameState.getTiles()::get);
    }

    @Override
    public @Nullable StructureType getMemory(Player player) {
        return Optional
                .ofNullable(data.getMemory(player.getId()))
                .map(gameState.getRuleset()::getStructureType)
                .orElse(null);
    }

    @Override
    public Tile setMemory(Player player, @Nullable StructureType structureType) {
        data.setMemory(player.getId(), structureType == null ? null : structureType.getId());
        gameState.invalidateCache();
        return this;
    }

    @Override
    public List<Rule> getRules() {
        return List.of(getEffectiveType());
    }

    @Override
    public @Nullable Player getOwner() {
        return null;
    }

    @Override
    public @Unmodifiable List<Action> getActions() {
        return List.of();
    }

    @Override
    public Tile getTile() {
        return this;
    }

    private TerrainType getEffectiveType() {
        return Optional.ofNullable(getStructure()).<TerrainType>map(Structure::getType).orElse(getType());
    }

}
