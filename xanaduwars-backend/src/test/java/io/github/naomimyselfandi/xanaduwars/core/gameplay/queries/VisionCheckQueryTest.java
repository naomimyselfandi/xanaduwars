package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Tile;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VisionCheckQueryTest {

    @Mock
    private Unit actor;

    @Mock
    private Tile tile;

    @InjectMocks
    private VisionCheckQuery fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,3,true
            3,3,true
            3,2,false
            NaN,3,false
            """)
    void defaultValue(double distance, int vision, boolean expected) {
        when(actor.getDistance(tile)).thenReturn(distance);
        when(actor.getVision()).thenReturn(vision);
        assertThat(fixture.defaultValue()).isEqualTo(expected);
    }

}
