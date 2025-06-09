package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
@EqualsAndHashCode(onParam_ = @Nullable)
final class TileImpl implements Tile {

    private final TileData data;

    @Getter(onMethod_ = @Override)
    private final GameState gameState;

    private final Ruleset ruleset;

    private final Creator creator;

    @Override
    public TileId id() {
        return data.id();
    }

    @Override
    public TileType type() {
        return ruleset.tileType(data.type());
    }

    @Override
    public Set<TileTag> tags() {
        return type().tags();
    }

    @Override
    public MovementTable movementTable() {
        return switch (structure()) {
            case Structure structure -> structure.movementTable();
            case null -> type().movementTable();
        };
    }

    @Override
    public double cost(Unit unit) {
        return movementTable().cost(unit.tags());
    }

    @Override
    public double cover() {
        return switch (structure()) {
            case Structure structure -> structure.cover();
            case null -> type().cover();
        };
    }

    @Override
    public @Nullable Structure structure() {
        var structureData = data.structureData();
        return structureData == null ? null : new StructureImpl(structureData, this, ruleset);
    }

    @Override
    public @Nullable Unit unit() {
        return gameState.units().filter(unit -> equals(unit.location())).findFirst().orElse(null);
    }

    @Override
    public void createUnit(UnitType type, Player owner) {
        creator.createUnitData(id(), type.id(), owner.id());
    }

    @Override
    public void createStructure(StructureType type, Player owner) {
        creator.createStructureData(id(), type.id(), owner.id());
    }

    @Override
    public int distance(Tile that) {
        var thisId = this.id();
        var thatId = that.id();
        var dx = Math.abs(thisId.x() - thatId.x());
        var dy = Math.abs(thisId.y() - thatId.y());
        return dx + dy;
    }

    @Override
    public double distance(Physical that) {
        var thatTile = that.tile();
        return thatTile == null ? Double.NaN : distance(thatTile);
    }

    @Override
    public Stream<Tile> area(int radius) {
        return data.id().area(radius).map(gameState::maybeTile).filter(Objects::nonNull);
    }

    @Override
    public @Nullable Tile step(Direction direction) {
        return gameState.maybeTile(data.id().step(direction));
    }

    @Override
    public Tile tile() {
        return this;
    }

    @Override
    public String toString() {
        var id = data.id();
        return "Tile[x=%d, y=%d, type=%s]".formatted(id.x(), id.y(), type());
    }

}
