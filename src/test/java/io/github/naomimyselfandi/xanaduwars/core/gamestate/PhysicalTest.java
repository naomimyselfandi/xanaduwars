package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PhysicalTest {

    @Mock
    private Tile tile0, tile1;

    @Mock
    private Unit unit0, unit1;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void getDistance_WhenBothElementsHaveTiles_ThenDelegates(boolean thisHasTile, boolean thatHasTile, boolean ok) {
        var distance = random.nextInt();
        when(unit0.getTile()).thenReturn(thisHasTile ? tile0 : null);
        when(unit1.getTile()).thenReturn(thatHasTile ? tile1 : null);
        when(tile0.getDistance(tile1)).thenReturn(distance);
        when(unit0.getDistance(unit1)).thenCallRealMethod();
        assertThat(unit0.getDistance(unit1)).isEqualTo(ok ? distance : null);
    }

}
