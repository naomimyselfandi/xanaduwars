package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.PlayerId;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TileTypeId;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TileDataTest {

    @Test
    void memory() {
        var tile = new TileData();
        assertThat(tile.memory()).isEmpty();
        var map = Map.of(new PlayerId(1), new TileTypeId(2));
        assertThat(tile.memory(map)).isSameAs(tile);
        assertThat(tile.memory()).isEqualTo(map);
    }

}
