package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetVisionQueryTest {

    @Mock
    private Unit subject;

    @Mock
    private Tile target;

    private AssetVisionQuery fixture;

    @BeforeEach
    void setup() {
        fixture = new AssetVisionQuery(subject, target);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            3,2,true
            3,3,true
            3,4,false
            3,NaN,false
            """)
    void defaultValue(int vision, double distance, boolean expected) {
        if (Double.isNaN(distance)) {
            when(subject.getDistance(target)).thenReturn(Double.NaN);
        } else {
            when(subject.getVision()).thenReturn(vision);
            when(subject.getDistance(target)).thenReturn(distance);
        }
        assertThat(fixture.defaultValue()).isEqualTo(expected);
    }

}
