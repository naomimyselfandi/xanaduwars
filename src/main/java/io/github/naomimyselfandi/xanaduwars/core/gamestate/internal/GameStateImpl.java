package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.*;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Event;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.*;
import java.util.stream.IntStream;

final class GameStateImpl implements GameState {

    private final Map<Query<?>, Object> queryCache = new HashMap<>();
    private final Set<Query<?>> blackhole = new HashSet<>();

    private final List<EventObserver> eventObservers = new ArrayList<>();

    @Getter(onMethod_ = @Override)
    private final @Unmodifiable List<Player> players;

    @Getter(onMethod_ = @Override)
    private final @Unmodifiable SortedMap<TileId, Tile> tiles;
    private final SortedMap<StructureId, Structure> structures;
    private final SortedMap<UnitId, Unit> units;

    @Getter(onMethod_ = @Override)
    private final Ruleset ruleset;

    @VisibleForTesting final GameStateData gameStateData;
    @VisibleForTesting final QueryEvaluator queryEvaluator;
    @VisibleForTesting final CopyMachine copyMachine;
    @VisibleForTesting final Redactor redactor;
    @VisibleForTesting final SpellSlotHelper spellSlotHelper;

    @Getter(onMethod_ = @Override)
    private final boolean limitedCopy;

    GameStateImpl(
            Ruleset ruleset,
            GameStateData gameStateData,
            QueryEvaluator queryEvaluator,
            CopyMachine copyMachine,
            Redactor redactor, SpellSlotHelper spellSlotHelper
    ) {
        this(ruleset, gameStateData, queryEvaluator, copyMachine, redactor, spellSlotHelper, false);
    }

    private GameStateImpl(
            Ruleset ruleset,
            GameStateData gameStateData,
            QueryEvaluator queryEvaluator,
            CopyMachine copyMachine,
            Redactor redactor,
            SpellSlotHelper spellSlotHelper,
            boolean limitedCopy
    ) {
        this.ruleset = ruleset;
        this.gameStateData = gameStateData;
        this.queryEvaluator = queryEvaluator;
        this.copyMachine = copyMachine;
        this.redactor = redactor;
        this.spellSlotHelper = spellSlotHelper;
        this.limitedCopy = limitedCopy;
        players = initPlayers(gameStateData.getPlayers());
        structures = initStructures(gameStateData.getStructures());
        tiles = initTiles(gameStateData.getTiles());
        units = initUnits(gameStateData.getUnits());
    }

    @Override
    @SuppressWarnings("DeconstructionCanBeUsed") // It confuses the coverage report
    public <T> T evaluate(Query<T> query) {
        if (query instanceof Event<T> event) {
            queryCache.clear();
            var result = queryEvaluator.evaluate(this, query);
            for (var observer : eventObservers) {
                observer.accept(event);
            }
            if (query instanceof StructureDestructionEvent it) {
                var id = it.subject().getId();
                gameStateData.removeStructure(id);
                structures.remove(id);
            }
            if (query instanceof UnitDestructionEvent it) {
                var id = it.subject().getId();
                gameStateData.removeUnit(id);
                units.remove(id);
            }
            return result;
        } else if (queryCache.containsKey(query)) {
            // We can't use computeIfAbsent here; rules might evaluate more queries.
            @SuppressWarnings("unchecked")
            var cached = (T) queryCache.get(query);
            return cached;
        } else if (blackhole.contains(query)) {
            blackhole.remove(query);
            throw new IllegalStateException("%s depends on itself!".formatted(query));
        } else try {
            blackhole.add(query);
            var result = queryEvaluator.evaluate(this, query);
            queryCache.put(query, result);
            return result;
        } finally {
            blackhole.remove(query);
        }
    }

    @Override
    public void invalidateCache() {
        queryCache.clear();
    }

    @Override
    public GameState attachObserver(EventObserver eventObserver) {
        eventObservers.add(eventObserver);
        return this;
    }

    @Override
    public GameState limitedTo(Player player) {
        var copyState = copyMachine.copy(gameStateData);
        redactor.redact(this, copyState, player);
        var limited = true;
        return new GameStateImpl(ruleset, copyState, queryEvaluator, copyMachine, redactor, spellSlotHelper, limited);
    }

    @Override
    public Turn getTurn() {
        return gameStateData.getTurn();
    }

    @Override
    public GameState setTurn(Turn turn) {
        gameStateData.setTurn(turn);
        evaluate(new TurnStartEvent(getActivePlayer()));
        return this;
    }

    @Override
    public boolean isPassed() {
        return gameStateData.isPassed();
    }

    @Override
    public GameState setPassed(boolean passed) {
        gameStateData.setPassed(passed);
        invalidateCache();
        return this;
    }

    @Override
    public @UnmodifiableView SortedMap<StructureId, Structure> getStructures() {
        return Collections.unmodifiableSortedMap(structures);
    }

    @Override
    public @UnmodifiableView SortedMap<UnitId, Unit> getUnits() {
        return Collections.unmodifiableSortedMap(units);
    }

    @Override
    public Player getActivePlayer() {
        return players.get(gameStateData.getTurn().ordinal() % players.size());
    }

    @Override
    public Structure createStructure(Tile tile, StructureType type) {
        var tileId = tile.getId();
        var structureId = tileId.structureId();
        var created = gameStateData.createStructure(tileId, type.getId());
        var structure = new StructureImpl(created, this, structureId);
        structures.put(structureId, structure);
        return structure;
    }

    @Override
    public Unit createUnit(Tile tile, UnitType type) {
        var entry = gameStateData.createUnit(tile.getId(), type.getId());
        var unitId = entry.getKey();
        var unit = new UnitImpl(entry.getValue(), this, unitId);
        units.put(unitId, unit);
        return unit;
    }

    @Override
    public @Nullable Unit getUnit(Node location) {
        return gameStateData.findUnitId(location.getId()) instanceof UnitId id ? units.get(id) : null;
    }

    @Override
    public void moveUnit(Unit unit, Node destination) {
        var previousLocation = unit.getLocation();
        gameStateData.moveUnit(unit.getId(), destination.getId());
        evaluate(new MovementEvent(unit, previousLocation));
    }

    private List<Player> initPlayers(List<PlayerData> states) {
        return IntStream
                .range(0, states.size())
                .<Player>mapToObj(i -> new PlayerImpl(states.get(i), this, new PlayerId(i), spellSlotHelper))
                .toList();
    }

    private SortedMap<StructureId, Structure> initStructures(Map<StructureId, StructureData> states) {
        var result = new TreeMap<StructureId, Structure>();
        for (var entry : states.entrySet()) {
            result.put(entry.getKey(), new StructureImpl(entry.getValue(), this, entry.getKey()));
        }
        return result;
    }

    private SortedMap<TileId, Tile> initTiles(Map<TileId, TileData> states) {
        var result = new TreeMap<TileId, Tile>();
        for (var entry : states.entrySet()) {
            result.put(entry.getKey(), new TileImpl(entry.getValue(), this, entry.getKey()));
        }
        return Collections.unmodifiableSortedMap(result);
    }

    private SortedMap<UnitId, Unit> initUnits(Map<UnitId, UnitData> states) {
        var result = new TreeMap<UnitId, Unit>();
        for (var entry : states.entrySet()) {
            result.put(entry.getKey(), new UnitImpl(entry.getValue(), this, entry.getKey()));
        }
        return result;
    }

}
