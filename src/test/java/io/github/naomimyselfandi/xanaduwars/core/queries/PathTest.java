package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Tile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PathTest {

    @Mock
    private Tile a, b, c;

    @Test
    @SuppressWarnings("DataFlowIssue")
    void constructorPermitsValidPaths() {
        when(a.distance(b)).thenReturn(1);
        when(b.distance(c)).thenReturn(1);
        var path = new Path(a, new ArrayList<>(List.of(b, c)));
        assertThatThrownBy(() -> path.steps().clear()).isInstanceOf(UnsupportedOperationException.class);
        assertThat(path.steps()).containsExactly(b, c);
    }

    @Test
    void constructorProhibitsEmptyPaths() {
        assertThatThrownBy(() -> new Path(a, List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void constructorProhibitsPathsWithHoles(boolean problemAtStart) {
        if (problemAtStart) {
            lenient().when(a.distance(b)).thenReturn(2);
            lenient().when(b.distance(c)).thenReturn(1);
        } else {
            lenient().when(a.distance(b)).thenReturn(1);
            lenient().when(b.distance(c)).thenReturn(2);
        }
        assertThatThrownBy(() -> new Path(a, List.of(b, c))).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void constructorProhibitsPathsWithDoubledTiles(boolean problemAtStart) {
        if (problemAtStart) {
            lenient().when(a.distance(b)).thenReturn(0);
            lenient().when(b.distance(c)).thenReturn(1);
        } else {
            lenient().when(a.distance(b)).thenReturn(1);
            lenient().when(b.distance(c)).thenReturn(0);
        }
        assertThatThrownBy(() -> new Path(a, List.of(b, c))).isInstanceOf(IllegalArgumentException.class);
    }

}
