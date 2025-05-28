package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.UnitId;
import lombok.Getter;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

final class GameStateImpl extends AbstractGameState {

    private static final Predicate<Player> ALIVE = Predicate.not(Player::defeated);

    @Getter(onMethod_ = @Override)
    private final List<Player> players;

    @Getter(onMethod_ = @Override)
    private final List<List<Tile>> tiles;

    GameStateImpl(
            GameData gameData,
            Ruleset ruleset,
            GameDataFactory gameDataFactory,
            QueryEvaluator queryEvaluator,
            ActionPolicy actionPolicy,
            ActionExecutor actionExecutor
    ) {
        super(gameData, ruleset, gameDataFactory, queryEvaluator, actionPolicy, actionExecutor);
        this.players = gameData.players().stream().map(this::wrap).toList();
        this.tiles = IntStream
                .range(0, gameData.height())
                .boxed()
                .map(y -> IntStream
                        .range(0, gameData.width())
                        .mapToObj(x -> gameData.tileData(x, y))
                        .map(this::wrap)
                        .toList())
                .toList();
    }

    @Override
    public int turn() {
        return gameData.turn();
    }

    @Override
    public Player activePlayer() {
        return players.get(gameData.activePlayer().intValue());
    }

    @Override
    public void pass() {
        var candidate = players.stream().skip(gameData.activePlayer().intValue() + 1).filter(ALIVE).findFirst();
        if (candidate.isEmpty()) {
            gameData.turn(gameData.turn() + 1);
            candidate = players.stream().filter(ALIVE).findFirst();
        }
        candidate.map(Player::id).ifPresent(gameData::activePlayer);
        evaluate(new TurnStartEvent(activePlayer()));
    }

    @Override
    public Optional<Tile> tile(int x, int y) {
        if (x < 0 || y < 0 || x >= gameData.width() || y >= gameData.height()) {
            return Optional.empty();
        } else {
            return Optional.of(tiles.get(y).get(x));
        }
    }

    @Override
    public @Unmodifiable List<Unit> units() {
        return gameData.units().values().stream().map(this::wrap).toList();
    }

    @Override
    public Optional<Unit> unit(int id) {
        return Optional.ofNullable(gameData.units().get(new UnitId(id))).map(this::wrap);
    }

    @Override
    public Unit createUnit(Node location, UnitType type) {
        return wrap(gameData.createUnitData(location.id(), type.id()));
    }

    @Override
    public void destroyUnit(UnitData unitData) {
        gameData.units().remove(unitData.unitId());
    }

    private Player wrap(PlayerData playerData) {
        return new PlayerImpl(this, playerData);
    }

    private Tile wrap(TileData tileData) {
        return new TileImpl(this, tileData);
    }

    private Unit wrap(UnitData unitData) {
        return new UnitImpl(this, unitData);
    }

}
