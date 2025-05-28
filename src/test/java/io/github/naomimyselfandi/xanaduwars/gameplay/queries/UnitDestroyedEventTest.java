package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitDestroyedEventTest {

    @Mock
    private Unit unit;

    @Mock
    private Tile tile;

    @InjectMocks
    private UnitDestroyedEvent fixture;

    @Test
    void previousLocation() {
        when(unit.location()).thenReturn(tile);
        assertThat(fixture.previousLocation()).isEqualTo(tile);
    }

}
