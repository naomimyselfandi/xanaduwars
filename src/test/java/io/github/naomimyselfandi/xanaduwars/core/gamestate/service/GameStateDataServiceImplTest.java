package io.github.naomimyselfandi.xanaduwars.core.gamestate.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.StructureId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.TileId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.UnitId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.map.dto.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateDataServiceImplTest {

    @Mock
    private GameStateDataRepository gameStateDataRepository;

    @InjectMocks
    private GameStateDataServiceImpl fixture;

    @Captor
    private ArgumentCaptor<GameStateData> captor;

    @Test
    void get(SeededRng random) throws NoSuchEntityException {
        var id = random.<Id<GameStateData>>get();
        var gameStateData = random.<GameStateData>get();
        when(gameStateDataRepository.findById(id)).thenReturn(Optional.of(gameStateData));
        assertThat(fixture.get(id)).isEqualTo(gameStateData);
    }

    @Test
    void get_WhenTheDataDoesNotExist_ThenThrows(SeededRng random) {
        var id = random.<Id<GameStateData>>get();
        when(gameStateDataRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.get(id)).isInstanceOf(NoSuchEntityException.class);
    }

    @Test
    void create(SeededRng random) {
        var player0 = random.<MapPlayerDto>get();
        var player1 = random.not(player0).setTeam(random.not(player0.getTeam()));
        var structure = random.<MapStructureDto>get();
        var unit0 = random.<MapUnitDto>get();
        var unit1 = random.not(unit0);
        var tile00 = new MapTileDto().setType(random.get());
        var tile10 = new MapTileDto().setType(random.get()).setUnit(unit0);
        var tile01 = new MapTileDto().setType(random.get());
        var tile11 = new MapTileDto().setType(random.get()).setUnit(unit1).setStructure(structure);
        var version = random.<Version>get();
        var map = new MapDto()
                .setPlayers(List.of(player0, player1))
                .setTiles(List.of(List.of(tile00, tile10), List.of(tile01, tile11)));
        var saved = random.<GameStateData>get();
        when(gameStateDataRepository.save(any())).thenReturn(saved);
        assertThat(fixture.create(map, version)).isEqualTo(saved);
        verify(gameStateDataRepository).save(captor.capture());
        var data = captor.getValue();
        assertThat(data.getPlayers()).hasSize(2).containsExactly(
                new PlayerData().setTeam(player0.getTeam()),
                new PlayerData().setTeam(player1.getTeam()));
        assertThat(data.getTiles())
                .hasSize(4)
                .containsEntry(new TileId(0, 0), new TileData().setTypeId(tile00.getType()))
                .containsEntry(new TileId(1, 0), new TileData().setTypeId(tile10.getType()))
                .containsEntry(new TileId(0, 1), new TileData().setTypeId(tile01.getType()))
                .containsEntry(new TileId(1, 1), new TileData().setTypeId(tile11.getType()));
        assertThat(data.getStructures())
                .hasSize(1)
                .containsEntry(new StructureId(1, 1), new StructureData()
                        .setTypeId(structure.getType())
                        .setHp(structure.getHp())
                        .setPlayerId(structure.getOwner()));
        assertThat(data.getUnits())
                .hasSize(2)
                .containsEntry(new UnitId(0), new UnitData()
                        .setTypeId(unit0.getType())
                        .setHp(unit0.getHp())
                        .setReady(false)
                        .setPlayerId(unit0.getOwner())
                        .setLocationId(new TileId(1, 0)))
                .containsEntry(new UnitId(1), new UnitData()
                        .setTypeId(unit1.getType())
                        .setHp(unit1.getHp())
                        .setReady(false)
                        .setPlayerId(unit1.getOwner())
                        .setLocationId(new TileId(1, 1)));
    }

}
