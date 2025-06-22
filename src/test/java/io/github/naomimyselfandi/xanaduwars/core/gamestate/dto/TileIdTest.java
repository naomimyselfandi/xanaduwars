package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.StructureId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.TileId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class TileIdTest {

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(
                new TileId(0, 0), new TileId(1, 0), new TileId(2, 0),
                new TileId(0, 1), new TileId(1, 1), new TileId(2, 1),
                new TileId(0, 2), new TileId(1, 2), new TileId(2, 2)
        );
    }

    @Test
    void structureId(SeededRng random) {
        var tileId = random.<TileId>get();
        assertThat(tileId.structureId()).isEqualTo(new StructureId(tileId.x(), tileId.y()));
    }

}
