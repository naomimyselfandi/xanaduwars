package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Construction;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitLeftEvent;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CancelConstructionRuleTest {

    @Mock
    private Tile tile;

    @Mock
    private Unit unit;

    @Mock
    private UnitLeftEvent event;

    @InjectMocks
    private CancelConstructionRule fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void handles(boolean expected, SeededRng random) {
        var construction = new Construction(mock(), random.nextPercent());
        when(event.previousLocation()).thenReturn(tile);
        when(tile.construction()).thenReturn(Optional.of(construction).filter(_ -> expected));
        assertThat(fixture.handles(event, None.NONE)).isEqualTo(expected);
    }

    @Test
    void handles_WhenTheUnitLeftATransport_ThenFalse() {
        when(event.previousLocation()).thenReturn(unit);
        assertThat(fixture.handles(event, None.NONE)).isFalse();
        verify(unit, never()).location();
    }

    @Test
    void handle() {
        when(event.previousLocation()).thenReturn(tile);
        assertThat(fixture.handle(event, None.NONE)).isEqualTo(None.NONE);
        verify(tile).construction(null);
    }

}
