package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.UnitData;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
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
        return ruleset.unitTypes().get(data.unitType().index());
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
        return gameState.evaluate(new UnitStatQuery(this, UnitStat.VISION));
    }

    @Override
    public int speed() {
        return gameState.evaluate(new UnitStatQuery(this, UnitStat.SPEED));
    }

    @Override
    public Range range() {
        var minRange = gameState.evaluate(new UnitStatQuery(this, UnitStat.MIN_RANGE));
        var maxRange = gameState.evaluate(new UnitStatQuery(this, UnitStat.MAX_RANGE));
        return new Range(minRange, maxRange);
    }

    @Override
    public Map<NodeType, Scalar> damageTable() {
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
        for (var cargo : cargo()) {
            cargo.hp(Percent.ZERO);
        }
        gameState.evaluate(new UnitDestroyedEvent(this));
        gameState.destroyUnit(data);
    }

}
