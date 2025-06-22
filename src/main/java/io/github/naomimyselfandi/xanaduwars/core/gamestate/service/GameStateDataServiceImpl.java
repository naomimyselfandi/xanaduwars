package io.github.naomimyselfandi.xanaduwars.core.gamestate.service;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.TileId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateDataRepository;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.TileData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.map.dto.*;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class GameStateDataServiceImpl implements GameStateDataService {

    private final GameStateDataRepository gameStateDataRepository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public GameStateData get(Id<GameStateData> id) throws NoSuchEntityException {
        return gameStateDataRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public GameStateData create(MapDto map, Version version) {
        var gameStateData = new GameStateData().setVersion(version);
        createPlayers(map.getPlayers(), gameStateData);
        createTiles(map.getTiles(), gameStateData);
        return gameStateDataRepository.save(gameStateData);
    }

    private static void createPlayers(List<MapPlayerDto> players, GameStateData gameStateData) {
        for (var player : players) {
            gameStateData.getPlayers().add(new PlayerData().setTeam(player.getTeam()));
        }
    }

    private static void createTiles(List<List<MapTileDto>> tiles, GameStateData gameStateData) {
        var y = 0;
        for (var row : tiles) {
            var x = 0;
            for (var tile : row) {
                var tileId = new TileId(x, y);
                gameStateData.getTiles().put(tileId, new TileData().setTypeId(tile.getType()));
                if (tile.getStructure() instanceof MapStructureDto structure) {
                    gameStateData
                            .createStructure(tileId, structure.getType())
                            .setPlayerId(structure.getOwner())
                            .setHp(structure.getHp());
                }
                if (tile.getUnit() instanceof MapUnitDto unit) {
                    gameStateData
                            .createUnit(tileId, unit.getType())
                            .getValue()
                            .setPlayerId(unit.getOwner())
                            .setHp(unit.getHp());
                }
                x++;
            }
            y++;
        }
    }

}
