package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TagSet;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
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

    @RepeatedTest(3)
    void defaultValue(SeededRng random) {
        var foo = random.nextTag();
        var bar = random.nextTag();
        var fooCost = random.nextInt(2, 6) / 2.0;
        var barCost = random.nextInt(2, 6) / 2.0;
        when(unit.tags()).thenReturn(TagSet.of(foo, bar));
        when(tile.movementTable()).thenReturn(Map.of(foo, fooCost, bar, barCost));
        assertThat(new EntryQuery(unit, tile).defaultValue()).isEqualTo(Math.min(fooCost, barCost));
    }

    @Test
    void defaultValue_WhenNoneOfTheTagsArePresent_ThenNaN(SeededRng random) {
        var foo = random.nextTag();
        var bar = random.nextTag();
        when(unit.tags()).thenReturn(TagSet.of(foo));
        when(tile.movementTable()).thenReturn(Map.of(bar, random.nextDouble()));
        assertThat(new EntryQuery(unit, tile).defaultValue()).isNaN();
    }

}
