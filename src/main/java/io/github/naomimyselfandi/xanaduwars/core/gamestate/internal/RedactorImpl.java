package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.StructureData;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
class RedactorImpl implements Redactor {

    @Override
    public void redact(GameState visionProvider, GameStateData gameStateData, Player player) {
        redactPlayers(gameStateData, player.getTeam());
        redactStructures(visionProvider, gameStateData, player);
        redactTiles(visionProvider, gameStateData, player);
        redactUnits(visionProvider, gameStateData, player);
    }

    private void redactPlayers(GameStateData gameStateData, Team team) {
        for (var player : gameStateData.getPlayers()) {
            if (!player.getTeam().equals(team)) {
                player.setSupplies(0).setAether(0);
                var chosenSpells = player.getChosenSpells();
                var spellIds = chosenSpells.getSpellIds();
                var revelation = player.getChosenSpellRevelation();
                for (var i = 0; i < spellIds.size(); i++) {
                    if (!revelation.test(i)) spellIds.set(i, null);
                }
                chosenSpells.setSpellIds(spellIds);
            }
        }
    }

    private void redactStructures(GameState gameState, GameStateData gameStateData, Player player) {
        gameState.getStructures()
                .values()
                .stream()
                .filter(Predicate.not(player::canSee))
                .map(Structure::getId)
                .forEach(gameStateData.getStructures()::remove);
    }

    private void redactTiles(GameState gameState, GameStateData gameStateData, Player player) {
        gameState.getTiles()
                .values()
                .stream()
                .filter(Predicate.not(player::canSee))
                .forEach(tile -> tile.getMemory(player).ifPresent(structureType -> {
                    var structureId = tile.getId().structureId();
                    var structure = new StructureData().setTypeId(structureType.getId());
                    gameStateData.getStructures().put(structureId, structure);
                }));
    }

    private void redactUnits(GameState gameState, GameStateData gameStateData, Player player) {
        gameState.getUnits()
                .values()
                .stream()
                .filter(Predicate.not(player::canSee))
                .map(Unit::getId)
                .forEach(gameStateData.getUnits()::remove);
    }

}
