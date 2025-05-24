package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.core.Resource;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TileId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.VersionNumber;
import io.github.naomimyselfandi.xanaduwars.core.data.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
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

    private final UUID id = UUID.randomUUID();

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
    void createAndUpdateMap(RepetitionInfo repetitionInfo) {
        var map = new GameMap()
                .name("TestMap" + repetitionInfo.getCurrentRepetition())
                .dimensions(3, 2)
                .playerCount(3);
        lowLevelDataRepository.save(map.id(id));
        TestTransaction.end();
        TestTransaction.start();
        assertThat(gameMapRepository.count()).isOne();
        assertThat(gameMapRepository.findById(id)).contains(map);
        gameMapRepository
                .findById(id)
                .orElseThrow()
                .dimensions(2, 4)
                .playerCount(2)
                .createUnitData(new TileId(1, 2), 3);
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
        var map = new GameMap().name("TestMap").dimensions(2, 4).playerCount(2);
        map.createUnitData(new TileId(1, 2), 3);
        var data = gameDataFactory.create(map, new VersionNumber("1.2.3"));
        lowLevelDataRepository.save(data.id(id));
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
        var map = new GameMap().name("TestMap").dimensions(2, 4).playerCount(2);
        var data = gameDataFactory.create(map, new VersionNumber("1.2.3"));
        lowLevelDataRepository.save(data.id(id));
        TestTransaction.end();
        TestTransaction.start();
        data = gameDataRepository.findById(id).orElseThrow();
        var player = data.players().getFirst();
        player.resources(Map.of(Resource.SUPPLIES, 100));
        player.knownSpells(List.of(1, 2));
        player.activeSpells(List.of(1));
        var tile = data.tiles().getFirst();
        tile.memory(Map.of(new PlayerId(1), 2));
        TestTransaction.end();
        TestTransaction.start();
        data = gameDataRepository.findById(id).orElseThrow();
        player = data.players().getFirst();
        assertThat(player.resources())
                .containsOnlyKeys(Resource.values())
                .containsEntry(Resource.SUPPLIES, 100)
                .containsEntry(Resource.AETHER, 0)
                .containsEntry(Resource.FOCUS, 0);
        assertThat(player.knownSpells()).containsExactly(1, 2);
        assertThat(player.activeSpells()).containsExactly(1);
        tile = data.tiles().getFirst();
        assertThat(tile.memory()).hasSize(1).containsEntry(new PlayerId(1), 2);
    }

}
