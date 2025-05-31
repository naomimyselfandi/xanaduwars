package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VisionQueryTest {

    @Mock
    private Player player;

    @Mock
    private Unit unit;

    @InjectMocks
    private VisionQuery fixture;

    @ParameterizedTest
    @LogicalSource(value = LogicalSource.Op.AND, negated = true)
    void shouldShortCircuit(boolean enemy, boolean value, boolean expected) {
        when(player.isAlly(unit)).thenReturn(!enemy);
        assertThat(fixture.shouldShortCircuit(value)).isEqualTo(expected);
    }

}
