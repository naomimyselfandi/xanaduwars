package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class MapDtoTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,2,true
            2,1,false
            2,3,false
            """)
    void isRectangular(int row0, int row1, boolean rectangular, SeededRng random) {
        var dto = new MapDto().setTiles(List.of(
                IntStream.range(0, row0).<MapTileDto>mapToObj(_ -> random.get()).toList(),
                IntStream.range(0, row1).<MapTileDto>mapToObj(_ -> random.get()).toList()
        ));
        assertThat(dto.isRectangular()).isEqualTo(rectangular);
    }

}
