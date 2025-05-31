package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.UnitStat;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitStatQueryTest {

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit;

    @EnumSource
    @ParameterizedTest
    void defaultValue(UnitStat stat, SeededRng random) {
        when(unit.type()).thenReturn(unitType);
        var expected = switch (stat) {
            case VISION -> {
                var vision = random.nextInt();
                when(unitType.vision()).thenReturn(vision);
                yield vision;
            }
            case SPEED -> {
                var speed = random.nextInt();
                when(unitType.speed()).thenReturn(speed);
                yield speed;
            }
            case MIN_RANGE -> {
                var range = random.nextRange();
                when(unitType.range()).thenReturn(range);
                yield range.min();
            }
            case MAX_RANGE -> {
                var range = random.nextRange();
                when(unitType.range()).thenReturn(range);
                yield range.max();
            }
        };
        assertThat(new UnitStatQuery(unit, stat).defaultValue()).isEqualTo(expected);
    }

}
