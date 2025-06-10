package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.TileId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateDataTest {

    private TileData t0x0, t1x0, t2x0, t3x0, t0x1, t1x1, t2x1, t3x1;

    private GameStateData fixture;

    @BeforeEach
    void setup(SeededRng random) {
        t0x0 = random.nextTileData().setId(new TileId(0, 0));
        t1x0 = random.nextTileData().setId(new TileId(1, 0));
        t2x0 = random.nextTileData().setId(new TileId(2, 0));
        t3x0 = random.nextTileData().setId(new TileId(3, 0));
        t0x1 = random.nextTileData().setId(new TileId(0, 1));
        t1x1 = random.nextTileData().setId(new TileId(1, 1));
        t2x1 = random.nextTileData().setId(new TileId(2, 1));
        t3x1 = random.nextTileData().setId(new TileId(3, 1));
        fixture = new GameStateData()
                .setVersion(random.nextVersion())
                .setTurn(random.get())
                .setPlayerData(List.of(random.nextPlayerData()))
                .setTileData(List.of(t0x0, t1x0, t2x0, t3x0, t0x1, t1x1, t2x1, t3x1))
                .setUnitData(List.of(random.nextUnitData(), random.nextUnitData()));
    }

    @Test
    void getTileDataAt() {
        assertThat(fixture.tileDataAt(new TileId(0, 0))).contains(t0x0);
        assertThat(fixture.tileDataAt(new TileId(1, 0))).contains(t1x0);
        assertThat(fixture.tileDataAt(new TileId(2, 0))).contains(t2x0);
        assertThat(fixture.tileDataAt(new TileId(3, 0))).contains(t3x0);
        assertThat(fixture.tileDataAt(new TileId(0, 1))).contains(t0x1);
        assertThat(fixture.tileDataAt(new TileId(1, 1))).contains(t1x1);
        assertThat(fixture.tileDataAt(new TileId(2, 1))).contains(t2x1);
        assertThat(fixture.tileDataAt(new TileId(3, 1))).contains(t3x1);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1,0
            0,-1
            4,0
            0,2
            """)
    void getTileDataAt_WhenTheIdIsOutOfBounds_ThenReturnsAnEmptyResult(int x, int y) {
        assertThat(fixture.tileDataAt(new TileId(x, y))).isEmpty();
    }

    @Test
    void getUnitDataOf() {
        for (var unitData : fixture.getUnitData()) {
            assertThat(fixture.unitDataOf(unitData.getId())).contains(unitData);
        }
    }

    @Test
    void getUnitDataAt() {
        for (var unitData : fixture.getUnitData()) {
            assertThat(fixture.unitDataAt(unitData.getLocation())).contains(unitData);
        }
    }

    @Test
    void width() {
        assertThat(fixture.width()).isEqualTo(4);
    }

    @Test
    void height() {
        assertThat(fixture.height()).isEqualTo(2);
    }

    @Test
    @SuppressWarnings({"EqualsWithItself", "AssertBetweenInconvertibleTypes"})
    void testEquals(SeededRng random) {
        assertThat(fixture).isNotEqualTo(null);
        assertThat(fixture).isEqualTo(fixture);
        var other = new GameStateData();
        assertThat(fixture).isNotEqualTo(other);
        assertThat(fixture.setId(random.get())).isSameAs(fixture);
        assertThat(fixture).isNotEqualTo(other);
        assertThat(other.setId(random.get())).isSameAs(other);
        assertThat(fixture).isNotEqualTo(other);
        assertThat(other.setId(fixture.getId())).isSameAs(other);
        assertThat(fixture).isEqualTo(other);
        assertThat(fixture).isNotEqualTo(fixture.getId());
    }

    @Test
    void testHashCode() {
        assertThat(fixture).hasSameHashCodeAs(GameStateData.class);
    }

}
