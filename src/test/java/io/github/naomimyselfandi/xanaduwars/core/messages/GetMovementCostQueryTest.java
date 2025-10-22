package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GetMovementCostQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Unit unit;

    @Mock
    private Tile tile;

    @InjectMocks
    private GetMovementCostQuery fixture;

    @RepeatedTest(4)
    void defaultValue(SeededRng random) {
        var foo = random.<UnitTag>get();
        var bar = random.not(foo);
        var baz = random.not(foo, bar);
        var bat = random.not(foo, bar, baz);
        var expected = (double) random.nextInt(2, 5);
        when(unit.getTags()).thenReturn(random.shuffle(foo, bar, baz));
        when(tile.getMovementTable()).thenReturn(Map.of(
                foo, (double) random.nextInt(5, 8),
                bar, expected,
                bat, 1.0
        ));
        assertThat(fixture.defaultValue(runtime)).isEqualTo(expected);
        verifyNoInteractions(runtime);
    }

    @Test
    void defaultValue_WhenNoTagMatches_ThenInfinity(SeededRng random) {
        var foo = random.<UnitTag>get();
        var bar = random.not(foo);
        var baz = random.not(foo, bar);
        var bat = random.not(foo, bar, baz);
        when(unit.getTags()).thenReturn(List.of(foo, bar));
        when(tile.getMovementTable()).thenReturn(Map.of(baz, random.nextDouble(), bat, random.nextDouble()));
        assertThat(fixture.defaultValue(runtime)).isEqualTo(Double.POSITIVE_INFINITY);
    }

}
