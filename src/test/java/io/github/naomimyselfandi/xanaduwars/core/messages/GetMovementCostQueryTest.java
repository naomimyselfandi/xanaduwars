package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitSelectorMap;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GetMovementCostQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private UnitSelectorMap<Double> movementTable;

    @Mock
    private Unit unit;

    @Mock
    private Tile tile;

    @InjectMocks
    private GetMovementCostQuery fixture;

    @BeforeEach
    void setup() {
        when(tile.getMovementTable()).thenReturn(movementTable);
    }

    @Test
    void defaultValue(SeededRng random) {
        var expected = random.nextDouble();
        when(movementTable.min(unit)).thenReturn(expected);
        assertThat(fixture.defaultValue(runtime)).isEqualTo(expected);
        verifyNoInteractions(runtime);
    }

    @Test
    void defaultValue_WhenNoTagMatches_ThenInfinity() {
        when(movementTable.min(unit)).thenReturn(null);
        assertThat(fixture.defaultValue(runtime)).isEqualTo(Double.POSITIVE_INFINITY);
    }

}
