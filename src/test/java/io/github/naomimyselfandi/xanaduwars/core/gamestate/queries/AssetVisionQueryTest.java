package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import org.jetbrains.annotations.Nullable;
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
    private Tile tile, target;

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
            3,,false
            """)
    void defaultValue(int vision, @Nullable Integer distance, boolean expected) {
        if (distance == null) {
            when(subject.getTile()).thenReturn(null);
        } else {
            when(subject.getVision()).thenReturn(vision);
            when(subject.getTile()).thenReturn(tile);
            when(tile.getDistance(target)).thenReturn(distance);
        }
        assertThat(fixture.defaultValue()).isEqualTo(expected);
    }

}
