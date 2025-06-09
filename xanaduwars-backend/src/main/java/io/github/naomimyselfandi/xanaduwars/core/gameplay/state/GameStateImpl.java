package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.GameStateId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@EqualsAndHashCode
class GameStateImpl implements GameState {

    private final Ruleset ruleset;
    private final GameStateData gameStateData;
    private final QueryEvaluator queryEvaluator;

    @Getter(onMethod_ = @Override)
    private final boolean preview;

    @EqualsAndHashCode.Exclude
    @Getter(onMethod_ = @Override)
    private final @Unmodifiable List<Player> players;

    @EqualsAndHashCode.Exclude
    @Getter(onMethod_ = @Override)
    private final @Unmodifiable List<Tile> tiles;

    GameStateImpl(Ruleset ruleset, GameStateData gameStateData, QueryEvaluator queryEvaluator, boolean preview) {
        this.ruleset = ruleset;
        this.gameStateData = gameStateData;
        this.queryEvaluator = queryEvaluator;
        this.preview = preview;
        this.players = initPlayers();
        this.tiles = initTiles();
    }

    @Override
    public @Nullable GameStateId id() {
        return gameStateData.id();
    }

    @Override
    public void pass() {
        gameStateData.pass(true);
    }

    @Override
    public <T> T evaluate(Query<T> query) {
        return queryEvaluator.evaluate(ruleset, query);
    }

    @Override
    public Player player(PlayerId id) {
        return players.get(id.playerId());
    }

    @Override
    public Player activePlayer() {
        return players.get(gameStateData.turn().turn() % gameStateData.playerData().size());
    }

    @Override
    public Tile tile(TileId id) {
        return tiles.get(gameStateData.tileDataIndex(id).orElseThrow());
    }

    @Override
    public @Nullable Tile maybeTile(TileId id) {
        return gameStateData.tileDataIndex(id).map(tiles::get).orElse(null);
    }

    @Override
    public Stream<Structure> structures() {
        return tiles.stream().map(Tile::structure).filter(Objects::nonNull);
    }

    @Override
    public Stream<Unit> units() {
        return gameStateData.unitData().stream().map(it -> new UnitImpl(it, this, ruleset));
    }

    @Override
    public Unit unit(UnitId id) {
        return new UnitImpl(gameStateData.unitDataOf(id).orElseThrow(), this, ruleset);
    }

    private List<Player> initPlayers() {
        return gameStateData
                .playerData()
                .stream()
                .<Player>map(data -> new PlayerImpl(data, this, ruleset))
                .toList();
    }

    private List<Tile> initTiles() {
        var creator = new CreatorImpl(gameStateData);
        return gameStateData
                .tileData()
                .stream()
                .<Tile>map(data -> new TileImpl(data, this, ruleset, creator))
                .toList();
    }

}
