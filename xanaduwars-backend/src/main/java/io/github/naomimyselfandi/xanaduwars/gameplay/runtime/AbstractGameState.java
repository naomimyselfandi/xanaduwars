package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.ext.FieldIteration;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameData;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameDataFactory;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.TileData;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
abstract class AbstractGameState implements AugmentedGameState {

    final GameData gameData;

    @Getter(onMethod_ = @Override)
    private final Ruleset ruleset;

    private final GameDataFactory gameDataFactory;

    @Delegate
    private final QueryEvaluator queryEvaluator;

    private final ActionPolicy actionPolicy;

    private final ActionExecutor actionExecutor;

    @Override
    public <S extends Actor> List<Action<? super S, ?>> actions(S user) {
        return actionPolicy.actions(ruleset, user);
    }

    @Override
    public <S extends Actor> void execute(List<ActionItem<S, ?>> items, S user) throws ActionException {
        try {
            actionExecutor.execute(items, user);
        } finally {
            updateMemory();
        }
    }

    private void updateMemory() {
        for (var row : tiles()) {
            for (var tile : row) {
                for (var player : players()) {
                    if (player.canSee(tile)) {
                        var tileId = tile.id();
                        var tileData = gameData.tileData(tileId.x(), tileId.y());
                        var memory = new HashMap<>(tileData.memory());
                        memory.put(player.id(), tileData.structureType());
                        memory.values().removeIf(Objects::isNull);
                        tileData.memory(memory);
                    }
                }
            }
        }
    }

    @Override
    public GameState asSeenBy(Player player) {
        var copy = copy();
        copy.redactUnits(player);
        copy.redactTiles(player);
        return copy;
    }

    private AbstractGameState copy() {
        var copyData = gameDataFactory.create(gameData);
        return new GameStateImpl(copyData, ruleset, gameDataFactory, queryEvaluator, actionPolicy, actionExecutor);
    }

    private void redactUnits(Player player) {
        var unitIds = units()
                .stream()
                .filter(player::canSee)
                .map(Unit::id)
                .collect(Collectors.toSet());
        gameData.units().keySet().retainAll(unitIds);
    }

    private void redactTiles(Player player) {
        for (var row : tiles()) {
            for (var tile : row) {
                if (!player.canSee(tile)) {
                    var id = tile.id();
                    var tileData = gameData.tileData(id.x(), id.y());
                    FieldIteration.forEachField(TileData.Fields.values(), field -> switch (field) {
                        case tileId, tileType, memory -> null;
                        case structureType -> tileData.structureType(tileData.memory().get(player.id()));
                        case owner -> tile.owner(null);
                        case hitpoints -> tile.hp(Percent.FULL);
                        case construction -> tile.construction(null);
                    });
                }
            }
        }
    }

}
