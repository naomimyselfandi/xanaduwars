package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class GameMapUpdateDtoTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,3,6,true
            3,2,6,true
            3,3,9,true
            2,3,5,false
            2,3,7,false
            """)
    void isRectangle(int width, int height, int tileCount, boolean expected, SeededRng random) {
        var tiles = IntStream.range(0, tileCount).<MapTileDto>mapToObj(_ -> random.get()).toList();
        var dto = new GameMapUpdateDto(random.get(), width, height, tiles, List.of(random.get(), random.get()));
        assertThat(dto.isRectangle()).isEqualTo(expected);
    }

}
