package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.CleanupEvent;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.PlayerTurnEvent;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.StructureDeathEvent;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.UnitDeathEvent;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class GameStateHelperImpl implements GameStateHelper {

    private final @Lazy GameStateHelper self;

    @Override
    public void redact(GameState gameState, GameStateData data, Player player) {
        var playerId = player.id();
        for (var tile : gameState.tiles()) {
            if (!player.canSee(tile)) {
                var tileData = data.tileDataAt(tile.id()).orElseThrow();
                if (tileData.memory().memory().get(playerId) instanceof StructureTypeId type) {
                    var structureData = new StructureData().type(type).hp(Asset.FULL_HP).complete(true);
                    tileData.structureData(structureData);
                } else {
                    tileData.structureData(null);
                }
            }
        }
        var unitIds = gameState.units().filter(player::canSee).map(Unit::id).collect(Collectors.toSet());
        data.unitData().removeIf(it -> !unitIds.contains(it.id()));
        var team = player.team();
        var teams = data.playerData().stream().map(PlayerData::team).distinct().count();
        data.playerData().stream().filter(it -> !it.team().equals(team)).forEach(it -> {
            it.supplies(0).aether(0);
            if (teams > 2) it.focus(0);
            it.spellSlots(new SpellSlotList(it.spellSlots().slots().stream().filter(SpellSlotData::revealed).toList()));
        });
    }

    @Override
    public void updateMemory(GameState gameState, GameStateData data) {
        for (var tile : gameState.tiles()) {
            var tileData = data.tileDataAt(tile.id()).orElseThrow();
            var memory = new HashMap<>(tileData.memory().memory());
            var structureData = tileData.structureData();
            for (var player : gameState.players()) {
                if (player.canSee(tile)) {
                    if (structureData == null) {
                        memory.remove(player.id());
                    } else {
                        memory.put(player.id(), structureData.type());
                    }
                }
            }
            tileData.memory(new Memory(memory));
        }
    }

    @Override
    public void cleanup(GameState gameState, GameStateData data) {
        var structures = gameState.structures().filter(it -> it.hp() <= 0).toList();
        var units = gameState.units().filter(it -> it.hp() <= 0).toList();
        for (var structure : structures) {
            gameState.evaluate(new StructureDeathEvent(structure));
        }
        for (var unit : units) {
            gameState.evaluate(new UnitDeathEvent(unit));
        }
        for (var structure : structures) {
            data.tileDataAt(structure.tile().id()).orElseThrow().structureData(null);
        }
        data.unitData().removeIf(it -> it.hp() <= 0);
        if (!structures.isEmpty() || !units.isEmpty()) {
            gameState.evaluate(new CleanupEvent(gameState));
            self.cleanup(gameState, data);
        }
    }

    @Override
    public void pass(GameState gameState, GameStateData gameStateData) {
        var players = gameState.players();
        var size = players.size();
        var base = gameStateData.turn().turn();
        for (var offset = 1; offset < size; offset++) {
            var turn = base + offset;
            var player = players.get(turn % size);
            if (!player.defeated()) {
                gameStateData.turn(new Turn(turn));
                var supplies = player.structures().map(Structure::type).mapToInt(StructureType::supplyIncome).sum();
                var aether = player.structures().map(Structure::type).mapToInt(StructureType::aetherIncome).sum();
                player.supplies(player.supplies() + supplies);
                player.aether(player.aether() + aether);
                player.units().forEach(it -> it.actionsThisTurn(List.of()));
                gameState.evaluate(new PlayerTurnEvent(player));
                var playerData = gameStateData.playerData().get(player.id().playerId());
                var slots = playerData.spellSlots().slots().stream().map(it -> it.withCasts(0)).toList();
                playerData.spellSlots(new SpellSlotList(slots));
                break;
            }
        }
    }

}
