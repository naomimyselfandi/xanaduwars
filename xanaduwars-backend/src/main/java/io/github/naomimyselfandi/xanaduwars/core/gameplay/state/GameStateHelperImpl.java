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
        var playerId = player.getId();
        for (var tile : gameState.getTiles()) {
            if (!player.canSee(tile)) {
                var tileData = data.tileDataAt(tile.getId()).orElseThrow();
                if (tileData.getMemory().memory().get(playerId) instanceof StructureTypeId type) {
                    var structureData = new StructureData().setType(type).setHp(Asset.FULL_HP).setComplete(true);
                    tileData.setStructureData(structureData);
                } else {
                    tileData.setStructureData(null);
                }
            }
        }
        var unitIds = gameState.getUnits().filter(player::canSee).map(Unit::getId).collect(Collectors.toSet());
        data.getUnitData().removeIf(it -> !unitIds.contains(it.getId()));
        var team = player.getTeam();
        var teams = data.getPlayerData().stream().map(PlayerData::getTeam).distinct().count();
        data.getPlayerData().stream().filter(it -> !it.getTeam().equals(team)).forEach(it -> {
            it.setSupplies(0).setAether(0);
            if (teams > 2) it.setFocus(0);
            var slots = it.getSpellSlots().slots().stream().filter(SpellSlotData::revealed).toList();
            it.setSpellSlots(new SpellSlotList(slots));
        });
    }

    @Override
    public void updateMemory(GameState gameState, GameStateData data) {
        for (var tile : gameState.getTiles()) {
            var tileData = data.tileDataAt(tile.getId()).orElseThrow();
            var memory = new HashMap<>(tileData.getMemory().memory());
            var structureData = tileData.getStructureData();
            for (var player : gameState.getPlayers()) {
                if (player.canSee(tile)) {
                    if (structureData == null) {
                        memory.remove(player.getId());
                    } else {
                        memory.put(player.getId(), structureData.getType());
                    }
                }
            }
            tileData.setMemory(new Memory(memory));
        }
    }

    @Override
    public void cleanup(GameState gameState, GameStateData data) {
        var structures = gameState.getStructures().filter(it -> it.getHp() <= 0).toList();
        var units = gameState.getUnits().filter(it -> it.getHp() <= 0).toList();
        for (var structure : structures) {
            gameState.evaluate(new StructureDeathEvent(structure));
        }
        for (var unit : units) {
            gameState.evaluate(new UnitDeathEvent(unit));
        }
        for (var structure : structures) {
            data.tileDataAt(structure.getTile().getId()).orElseThrow().setStructureData(null);
        }
        data.getUnitData().removeIf(it -> it.getHp() <= 0);
        if (!structures.isEmpty() || !units.isEmpty()) {
            gameState.evaluate(new CleanupEvent(gameState));
            self.cleanup(gameState, data);
        }
    }

    @Override
    public void pass(GameState gameState, GameStateData gameStateData) {
        var players = gameState.getPlayers();
        var size = players.size();
        var base = gameStateData.getTurn().turn();
        for (var offset = 1; offset < size; offset++) {
            var turn = base + offset;
            var player = players.get(turn % size);
            if (!player.isDefeated()) {
                gameStateData.setTurn(new Turn(turn));
                var supplies = player.getStructures().map(Structure::getType).mapToInt(StructureType::getSupplyIncome).sum();
                var aether = player.getStructures().map(Structure::getType).mapToInt(StructureType::getAetherIncome).sum();
                player.setSupplies(player.getSupplies() + supplies);
                player.setAether(player.getAether() + aether);
                player.getUnits().forEach(it -> it.setHistory(List.of()));
                gameState.evaluate(new PlayerTurnEvent(player));
                var playerData = gameStateData.getPlayerData().get(player.getId().playerId());
                var slots = playerData.getSpellSlots().slots().stream().map(it -> it.withCasts(0)).toList();
                playerData.setSpellSlots(new SpellSlotList(slots));
                break;
            }
        }
    }

}
