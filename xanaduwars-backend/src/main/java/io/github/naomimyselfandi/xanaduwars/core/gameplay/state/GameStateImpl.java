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
    public @Nullable GameStateId getId() {
        return gameStateData.getId();
    }

    @Override
    public void pass() {
        gameStateData.setPass(true);
    }

    @Override
    public <T> T evaluate(Query<T> query) {
        return queryEvaluator.evaluate(ruleset, query);
    }

    @Override
    public Player getPlayer(PlayerId id) {
        return players.get(id.playerId());
    }

    @Override
    public Player getActivePlayer() {
        return players.get(gameStateData.getTurn().turn() % gameStateData.getPlayerData().size());
    }

    @Override
    public Tile getTile(TileId id) {
        return tiles.get(gameStateData.tileDataIndex(id).orElseThrow());
    }

    @Override
    public @Nullable Tile findTile(TileId id) {
        return gameStateData.tileDataIndex(id).map(tiles::get).orElse(null);
    }

    @Override
    public Stream<Structure> getStructures() {
        return tiles.stream().map(Tile::getStructure).filter(Objects::nonNull);
    }

    @Override
    public Stream<Unit> getUnits() {
        return gameStateData.getUnitData().stream().map(it -> new UnitImpl(it, this, ruleset));
    }

    @Override
    public Unit getUnit(UnitId id) {
        return new UnitImpl(gameStateData.unitDataOf(id).orElseThrow(), this, ruleset);
    }

    private List<Player> initPlayers() {
        return gameStateData
                .getPlayerData()
                .stream()
                .<Player>map(data -> new PlayerImpl(data, this, ruleset))
                .toList();
    }

    private List<Tile> initTiles() {
        var creator = new CreatorImpl(gameStateData);
        return gameStateData
                .getTileData()
                .stream()
                .<Tile>map(data -> new TileImpl(data, this, ruleset, creator))
                .toList();
    }

}
