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
class StructureIdTest {

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(
                new StructureId(0, 0), new StructureId(1, 0), new StructureId(2, 0),
                new StructureId(0, 1), new StructureId(1, 1), new StructureId(2, 1),
                new StructureId(0, 2), new StructureId(1, 2), new StructureId(2, 2)
        );
    }

    @Test
    void tileId(SeededRng random) {
        var structureId = random.<StructureId>get();
        assertThat(structureId.tileId()).isEqualTo(new TileId(structureId.x(), structureId.y()));
    }

}
