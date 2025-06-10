package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.History;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.SpeedQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true, onParam_ = @Nullable)
final class UnitImpl extends AbstractAsset<UnitData> implements Unit {

    @Getter(onMethod_ = @Override)
    private final GameState gameState;

    private final Ruleset ruleset;

    UnitImpl(UnitData data, GameState gameState, Ruleset ruleset) {
        super(data);
        this.gameState = gameState;
        this.ruleset = ruleset;
    }

    @Override
    public UnitId getId() {
        return data.getId();
    }

    @Override
    public UnitType getType() {
        return ruleset.getUnitType(data.getType());
    }

    @Override
    public Set<UnitTag> getTags() {
        return getType().getTags();
    }

    @Override
    public Node getLocation() {
        return switch (data.getLocation()) {
            case TileId tileId -> gameState.getTile(tileId);
            case UnitId unitId -> gameState.getUnit(unitId);
        };
    }

    @Override
    public Unit setLocation(Node location) {
        data.setLocation(switch (location) {
            case Tile tile -> tile.getId();
            case Unit unit -> unit.getId();
        });
        return this;
    }

    @Override
    public @Nullable Tile getTile() {
        return data.getLocation() instanceof TileId tileId ? gameState.getTile(tileId) : null;
    }

    @Override
    public @Nullable Terrain getTerrain() {
        var tile = getTile();
        return tile instanceof Tile it && it.getStructure() instanceof Structure structure ? structure : tile;
    }

    @Override
    public double getDistance(Physical that) {
        var tile = this.getTile();
        return tile == null ? Double.NaN : tile.getDistance(that);
    }

    @Override
    public @Nullable Unit getCargo() {
        return gameState.getUnits().filter(unit -> equals(unit.getLocation())).findFirst().orElse(null);
    }

    @Override
    public Hangar getHangar() {
        return getType().getHangar();
    }

    @Override
    public int getSpeed() {
        return gameState.evaluate(new SpeedQuery(this));
    }

    @Override
    public @Unmodifiable List<Name> getHistory() {
        return data.getHistory().names();
    }

    @Override
    public Unit setHistory(List<Name> actionsThisTurn) {
        data.setHistory(new History(actionsThisTurn));
        return this;
    }

    @Override
    @SuppressWarnings("RedundantTypeArguments")
    public @Unmodifiable List<Action> getAction() {
        var type = getType();
        return Stream.of(
                type.getWeapons().stream(),
                type.getAbilities().stream(),
                ruleset.getCommonUnitActions().stream()
        ).<Action>flatMap(Function.identity()).toList();
    }

    @Override
    public String toString() {
        return "Unit[id=%d, type=%s]".formatted(data.getId().unitId(), getType());
    }

}
