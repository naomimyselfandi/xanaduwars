package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.data.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.queries.UnitMovedEvent;
import io.github.naomimyselfandi.xanaduwars.core.queries.UnitStat;
import io.github.naomimyselfandi.xanaduwars.core.queries.UnitStatQuery;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.*;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

final class UnitImpl extends AbstractNode<UnitId, UnitType, UnitData> implements Unit {

    UnitImpl(AugmentedGameState gameState, UnitData data) {
        super(gameState, data.unitId(), data);
    }

    @Override
    public UnitType type() {
        return ruleset.unitTypes().get(data.unitType());
    }

    @Override
    public Node location() {
        return (switch (data.location()) {
            case TileId tileId -> gameState.tile(tileId.x(), tileId.y());
            case UnitId unitId -> gameState.unit(unitId.intValue());
        }).orElseThrow();
    }

    @Override
    public void location(Node location) {
        var previousLocation = location();
        if (location.equals(previousLocation)) return;
        if (location instanceof Tile tile && tile.unit().isPresent()) {
            var message = "%s cannot move to %s because there is already a unit there.".formatted(this, tile);
            throw new IllegalStateException(message);
        }
        data.location(location.id());
        gameState.evaluate(new UnitMovedEvent(this, previousLocation));
    }

    @Override
    public Optional<Tile> tile() {
        return Optional.ofNullable(switch (location()) {
            case Tile tile -> tile;
            case Unit _ -> null;
        });
    }

    @Override
    public @Unmodifiable List<Unit> cargo() {
        return gameState.units().stream().filter(it -> equals(it.location())).toList();
    }

    @Override
    public boolean canAct() {
        return data.canAct();
    }

    @Override
    public void canAct(boolean canAct) {
        data.canAct(canAct);
    }

    @Override
    public int vision() {
        return gameState.evaluate(new UnitStatQuery(this, UnitStat.VISION), type().vision());
    }

    @Override
    public int speed() {
        return gameState.evaluate(new UnitStatQuery(this, UnitStat.SPEED), type().speed());
    }

    @Override
    public Range range() {
        var baseRange = type().range();
        var minRange = gameState.evaluate(new UnitStatQuery(this, UnitStat.MIN_RANGE), baseRange.min());
        var maxRange = gameState.evaluate(new UnitStatQuery(this, UnitStat.MAX_RANGE), baseRange.max());
        return new Range(minRange, maxRange);
    }

    @Override
    public Map<NodeType, Percent> damageTable() {
        return type().damageTable();
    }

    @Override
    public TagSet hangar() {
        return type().hangar();
    }

    @Override
    public List<Action<Unit, ?>> abilities() {
        return type().abilities();
    }

    @Override
    void onDestruction() {
        gameState.destroyUnit(data);
    }

}
