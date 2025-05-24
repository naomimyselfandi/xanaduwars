package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Tile;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntryQueryTest {

    @Mock
    private Unit unit;

    @Mock
    private Tile tile;

    @InjectMocks
    private EntryQuery fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            NaN,true
            0.0,false
            0.1,false
            1.0,false
            5.0,false
            """)
    void shouldShortCircuit(double value, boolean expected) {
        assertThat(fixture.shouldShortCircuit(value)).isEqualTo(expected);
        verifyNoInteractions(unit, tile);
    }

}
