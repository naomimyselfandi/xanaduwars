package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

record UnitImpl(UnitData data, @Getter GameState gameState, @Getter UnitId id) implements Unit {

    @Override
    public UnitType getType() {
        return gameState.getRuleset().getUnitType(data.getTypeId());
    }

    @Override
    public Unit setType(UnitType type) {
        data.setTypeId(type.getId());
        gameState.evaluate(new GenericEvent(this));
        return this;
    }

    @Override
    public @Unmodifiable Set<UnitTag> getTags() {
        return gameState.evaluate(new UnitTagQuery(this));
    }

    @Override
    public int getVision() {
        return gameState.evaluate(new VisionRangeQuery(this));
    }

    @Override
    public int getSpeed() {
        return gameState.evaluate(new SpeedQuery(this));
    }

    @Override
    public Hp getHp() {
        return data.getHp();
    }

    @Override
    public Asset setHp(Hp hp) {
        data.setHp(hp);
        gameState.evaluate(hp.equals(Hp.ZERO) ? new UnitDestructionEvent(this) : new GenericEvent(this));
        return this;
    }

    @Override
    public Node getLocation() {
        return switch (data.getLocationId()) {
            case TileId t -> gameState.getTiles().get(t);
            case UnitId u -> gameState.getUnits().get(u);
        };
    }

    @Override
    public Optional<Tile> getTile() {
        return switch (data.getLocationId()) {
            case TileId t -> Optional.of(gameState.getTiles().get(t));
            case UnitId _ -> Optional.empty();
        };
    }

    @Override
    public Optional<Unit> getCargo() {
        return gameState.getUnit(this);
    }

    @Override
    public boolean isReady() {
        return data.isReady();
    }

    @Override
    public Unit setReady(boolean ready) {
        data.setReady(ready);
        gameState.evaluate(new GenericEvent(this));
        return this;
    }

    @Override
    public Optional<Player> getOwner() {
        return Optional.ofNullable(data.getPlayerId()).map(PlayerId::playerId).map(gameState.getPlayers()::get);
    }

    @Override
    public Asset setOwner(@Nullable Player owner) {
        data.setPlayerId(owner == null ? null : owner.getId());
        gameState.evaluate(new GenericEvent(this));
        return this;
    }

    @Override
    public @Unmodifiable List<Action> getActions() {
        var ruleset = gameState.getRuleset();
        var result = Stream.<Action>builder();
        result.add(ruleset.getMoveAction());
        result.add(ruleset.getDropAction());
        getType().getWeapons().stream().map(it -> new WeaponWrapper(this, it)).forEach(result);
        getType().getActions().forEach(result);
        result.add(ruleset.getWaitAction());
        return result.build().toList();
    }

    @Override
    public List<Rule> getRules() {
        var result = Stream.<Rule>builder();
        result.add(getType());
        getOwner().stream().map(Player::getRules).flatMap(List::stream).forEach(result);
        getTile().stream().map(Tile::getRules).flatMap(List::stream).forEach(result);
        return result.build().toList();
    }

}
