package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class TileIdTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,NORTH,0,-1
            0,0,EAST,1,0,
            0,0,SOUTH,0,1
            0,0,WEST,-1,0
            1,3,NORTH,1,2
            3,2,EAST,4,2
            2,3,SOUTH,2,4
            1,2,WEST,0,2
            """)
    void step(int x0, int y0, Direction direction, int x1, int y1) {
        assertThat(new TileId(x0, y0).step(direction)).isEqualTo(new TileId(x1, y1));
    }

    @Test
    void area() {
        assertThat(new TileId(1, 2).area(2)).containsExactly(
                new TileId(1, 0),
                new TileId(0, 1),
                new TileId(1, 1),
                new TileId(2, 1),
                new TileId(-1, 2),
                new TileId(0, 2),
                new TileId(1, 2),
                new TileId(2, 2),
                new TileId(3, 2),
                new TileId(0, 3),
                new TileId(1, 3),
                new TileId(2, 3),
                new TileId(1, 4)
        );
    }

}
