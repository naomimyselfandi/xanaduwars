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
    public TileId getId() {
        return data.getId();
    }

    @Override
    public TileType getType() {
        return ruleset.getTileTypes(data.getType());
    }

    @Override
    public Set<TileTag> getTags() {
        return getType().getTags();
    }

    @Override
    public MovementTable getMovementTable() {
        return switch (getStructure()) {
            case Structure structure -> structure.getMovementTable();
            case null -> getType().getMovementTable();
        };
    }

    @Override
    public double getMovementCost(Unit unit) {
        return getMovementTable().cost(unit.getTags());
    }

    @Override
    public double getCover() {
        return switch (getStructure()) {
            case Structure structure -> structure.getCover();
            case null -> getType().getCover();
        };
    }

    @Override
    public @Nullable Structure getStructure() {
        var structureData = data.getStructureData();
        return structureData == null ? null : new StructureImpl(structureData, this, ruleset);
    }

    @Override
    public @Nullable Unit getUnit() {
        return gameState.getUnits().filter(unit -> equals(unit.getLocation())).findFirst().orElse(null);
    }

    @Override
    public void createUnit(UnitType type, Player owner) {
        creator.createUnitData(getId(), type.getId(), owner.getId());
    }

    @Override
    public void createStructure(StructureType type, Player owner) {
        creator.createStructureData(getId(), type.getId(), owner.getId());
    }

    @Override
    public int getDistance(Tile that) {
        var thisId = this.getId();
        var thatId = that.getId();
        var dx = Math.abs(thisId.x() - thatId.x());
        var dy = Math.abs(thisId.y() - thatId.y());
        return dx + dy;
    }

    @Override
    public double getDistance(Physical that) {
        var thatTile = that.getTile();
        return thatTile == null ? Double.NaN : getDistance(thatTile);
    }

    @Override
    public Stream<Tile> area(int radius) {
        return data.getId().area(radius).map(gameState::findTile).filter(Objects::nonNull);
    }

    @Override
    public @Nullable Tile step(Direction direction) {
        return gameState.findTile(data.getId().step(direction));
    }

    @Override
    public Tile getTile() {
        return this;
    }

    @Override
    public String toString() {
        var id = data.getId();
        return "Tile[x=%d, y=%d, type=%s]".formatted(id.x(), id.y(), getType());
    }

}
