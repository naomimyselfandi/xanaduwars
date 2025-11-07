package io.github.naomimyselfandi.xanaduwars.map.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class GameMapTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,3,6,true
            3,2,6,true
            3,3,9,true
            2,3,5,false
            2,3,7,false
            """)
    void isRectangle(int width, int height, int tiles, boolean expected, SeededRng random) {
        var map = new GameMap().setWidth(width).setHeight(height);
        for (var i = 0; i < tiles; i++) {
            map.getTiles().add(random.get());
        }
        assertThat(map.isRectangle()).isEqualTo(expected);
    }

}
