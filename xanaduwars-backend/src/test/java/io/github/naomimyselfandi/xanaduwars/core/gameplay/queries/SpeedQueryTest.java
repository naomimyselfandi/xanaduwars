package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SpeedQueryTest {

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit;

    @InjectMocks
    private SpeedQuery fixture;

    @RepeatedTest(2)
    void defaultValue(SeededRng random) {
        var speed = random.nextInt();
        when(unitType.getSpeed()).thenReturn(speed);
        when(unit.getType()).thenReturn(unitType);
        assertThat(fixture.defaultValue()).isEqualTo(speed);
    }

}
