package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.TileData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.UnitData;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class CopyMachineHelperTest {

    private final CopyMachineHelper fixture = new CopyMachineHelper() {

        @Override
        public PlayerData copy(PlayerData source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TileData copy(TileData source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UnitData copy(UnitData source) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void copy_TileId(SeededRng random) {
        var tileId = random.<TileId>get();
        assertThat(fixture.copy(tileId)).isEqualTo(tileId);
    }

    @Test
    void copy_UnitId(SeededRng random) {
        var unitId = random.<UnitId>get();
        assertThat(fixture.copy(unitId)).isEqualTo(unitId);
    }

}
