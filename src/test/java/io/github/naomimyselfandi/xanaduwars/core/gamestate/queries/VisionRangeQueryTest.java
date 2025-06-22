package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VisionRangeQueryTest {

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit;

    @InjectMocks
    private VisionRangeQuery fixture;

    @Test
    void defaultValue(SeededRng random) {
        var defaultValue = random.nextInt();
        when(unit.getType()).thenReturn(unitType);
        when(unitType.getVision()).thenReturn(defaultValue);
        assertThat(fixture.defaultValue()).isEqualTo(defaultValue);
    }

}
