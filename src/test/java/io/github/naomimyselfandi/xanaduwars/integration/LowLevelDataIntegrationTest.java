package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Rollback(false)
class LowLevelDataIntegrationTest extends AbstractIntegrationTest {

    private UUID id;

    @Autowired
    private GameDataFactory gameDataFactory;

    @Autowired
    private LowLevelDataRepository lowLevelDataRepository;

    @Autowired
    private GameMapRepository gameMapRepository;

    @Autowired
    private GameDataRepository gameDataRepository;

    @AfterEach
    void teardown() {
        try {
            if (TestTransaction.isActive()) {
                TestTransaction.end();
            }
        } finally {
            TestTransaction.start();
            lowLevelDataRepository.deleteById(id);
        }
    }

    @RepeatedTest(3)
    void createAndUpdateMap() {
        var map = new GameMap().dimensions(3, 2).playerCount(3);
        id = lowLevelDataRepository.save(map).id();
        TestTransaction.end();
        TestTransaction.start();
        assertThat(gameMapRepository.count()).isOne();
        assertThat(gameMapRepository.findById(id)).contains(map);
        gameMapRepository
                .findById(id)
                .orElseThrow()
                .dimensions(2, 4)
                .playerCount(2)
                .createUnitData(new TileId(1, 2), new UnitTypeId(3));
        TestTransaction.end();
        TestTransaction.start();
        map = gameMapRepository.findById(id).orElseThrow();
        assertThat(map.width()).isEqualTo(2);
        assertThat(map.height()).isEqualTo(4);
        assertThat(map.players()).hasSize(2);
        assertThat(map.units()).hasSize(1);
        assertThat(map.players()).allSatisfy(player -> assertThat(player.resources())
                .containsOnlyKeys(Resource.values())
                .values()
                .allSatisfy(value -> assertThat(value).isZero()));
    }

    @Test
    void createGameData() {
        var map = new GameMap().dimensions(2, 4).playerCount(2);
        map.createUnitData(new TileId(1, 2), new UnitTypeId(3));
        var data = gameDataFactory.create(map, new Version("1.2.3"));
        id = lowLevelDataRepository.save(data).id();
        TestTransaction.end();
        TestTransaction.start();
        assertThat(gameDataRepository.findById(id)).contains(data);
        data = gameDataRepository.findById(id).orElseThrow();
        assertThat(data.width()).isEqualTo(map.width());
        assertThat(data.height()).isEqualTo(map.height());
        assertThat(data.players()).hasSameSizeAs(map.players());
        assertThat(data.units()).hasSameSizeAs(map.units());
    }

    @Test
    void updateCollections() {
        var map = new GameMap().dimensions(2, 4).playerCount(2);
        var data = gameDataFactory.create(map, new Version("1.2.3"));
        id = lowLevelDataRepository.save(data).id();
        TestTransaction.end();
        TestTransaction.start();
        data = gameDataRepository.findById(id).orElseThrow();
        var player = data.players().getFirst();
        player.resources(Map.of(Resource.SUPPLIES, 100));
        player.knownSpells(List.of(new SpellTypeId(1), new SpellTypeId(2)));
        player.activeSpells(List.of(new SpellTypeId(1)));
        var tile = data.tiles().getFirst();
        tile.memory(Map.of(new PlayerId(1), new TileTypeId(2)));
        TestTransaction.end();
        TestTransaction.start();
        data = gameDataRepository.findById(id).orElseThrow();
        player = data.players().getFirst();
        assertThat(player.resources())
                .containsOnlyKeys(Resource.values())
                .containsEntry(Resource.SUPPLIES, 100)
                .containsEntry(Resource.AETHER, 0)
                .containsEntry(Resource.FOCUS, 0);
        assertThat(player.knownSpells()).containsExactly(new SpellTypeId(1), new SpellTypeId(2));
        assertThat(player.activeSpells()).containsExactly(new SpellTypeId(1));
        tile = data.tiles().getFirst();
        assertThat(tile.memory()).hasSize(1).containsEntry(new PlayerId(1), new TileTypeId(2));
    }

}
