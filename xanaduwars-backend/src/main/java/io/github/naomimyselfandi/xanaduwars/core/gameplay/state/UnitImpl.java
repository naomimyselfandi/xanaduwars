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
    public UnitId id() {
        return data.id();
    }

    @Override
    public UnitType type() {
        return ruleset.unitType(data.type());
    }

    @Override
    public Set<UnitTag> tags() {
        return type().tags();
    }

    @Override
    public Node location() {
        return switch (data.location()) {
            case TileId tileId -> gameState.tile(tileId);
            case UnitId unitId -> gameState.unit(unitId);
        };
    }

    @Override
    public Unit location(Node location) {
        data.location(switch (location) {
            case Tile tile -> tile.id();
            case Unit unit -> unit.id();
        });
        return this;
    }

    @Override
    public @Nullable Tile tile() {
        return data.location() instanceof TileId tileId ? gameState.tile(tileId) : null;
    }

    @Override
    public @Nullable Terrain terrain() {
        var tile = tile();
        return tile instanceof Tile it && it.structure() instanceof Structure structure ? structure : tile;
    }

    @Override
    public double distance(Physical that) {
        var tile = this.tile();
        return tile == null ? Double.NaN : tile.distance(that);
    }

    @Override
    public @Nullable Unit cargo() {
        return gameState.units().filter(unit -> equals(unit.location())).findFirst().orElse(null);
    }

    @Override
    public Hangar hangar() {
        return type().hangar();
    }

    @Override
    public int speed() {
        return gameState.evaluate(new SpeedQuery(this));
    }

    @Override
    public @Unmodifiable List<Name> actionsThisTurn() {
        return data.history().names();
    }

    @Override
    public Unit actionsThisTurn(List<Name> actionsThisTurn) {
        data.history(new History(actionsThisTurn));
        return this;
    }

    @Override
    @SuppressWarnings("RedundantTypeArguments")
    public @Unmodifiable List<Action> actions() {
        var type = type();
        return Stream.of(
                type.weapons().stream(),
                type.abilities().stream(),
                ruleset.commonUnitActions().stream()
        ).<Action>flatMap(Function.identity()).toList();
    }

    @Override
    public String toString() {
        return "Unit[id=%d, type=%s]".formatted(data.id().unitId(), type());
    }

}
