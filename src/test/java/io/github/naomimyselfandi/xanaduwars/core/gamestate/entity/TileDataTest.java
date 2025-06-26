package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class TileDataTest {

    private TileData fixture;

    @BeforeEach
    void setup() {
        fixture = new TileData();
    }

    @Test
    void getMemory(SeededRng random) {
        var playerId0 = random.<PlayerId>get();
        var playerId1 = random.<PlayerId>get();
        var structureTypeId0 = random.<StructureTypeId>get();
        var structureTypeId1 = random.<StructureTypeId>get();
        assertThat(fixture.getMemory(playerId0)).isEmpty();
        assertThat(fixture.getMemory(playerId1)).isEmpty();
        assertThat(fixture.setMemory(playerId0, structureTypeId0)).isSameAs(fixture);
        assertThat(fixture.getMemory(playerId0)).contains(structureTypeId0);
        assertThat(fixture.getMemory(playerId1)).isEmpty();
        assertThat(fixture.setMemory(playerId1, structureTypeId1)).isSameAs(fixture);
        assertThat(fixture.getMemory(playerId0)).contains(structureTypeId0);
        assertThat(fixture.getMemory(playerId1)).contains(structureTypeId1);
        assertThat(fixture.setMemory(playerId0, structureTypeId1)).isSameAs(fixture);
        assertThat(fixture.getMemory(playerId0)).contains(structureTypeId1);
        assertThat(fixture.getMemory(playerId1)).contains(structureTypeId1);
        assertThat(fixture.setMemory(playerId1, null)).isSameAs(fixture);
        assertThat(fixture.getMemory(playerId0)).contains(structureTypeId1);
        assertThat(fixture.getMemory(playerId1)).isEmpty();
        assertThat(fixture.setMemory(playerId0, null)).isSameAs(fixture);
        assertThat(fixture.getMemory(playerId0)).isEmpty();
        assertThat(fixture.getMemory(playerId1)).isEmpty();
    }

}
