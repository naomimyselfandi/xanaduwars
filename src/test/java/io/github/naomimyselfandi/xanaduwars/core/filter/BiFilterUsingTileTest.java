package io.github.naomimyselfandi.xanaduwars.core.filter;

import io.github.naomimyselfandi.xanaduwars.core.Tile;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiFilterUsingTileTest {

    @Mock
    private Tile foo;

    @Mock
    private Unit bar, baz;

    @Mock
    private BiFilter<Unit, Tile> filter;

    @InjectMocks
    private BiFilterUsingTile<Unit> fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected) {
        when(bar.tile()).thenReturn(Optional.of(foo));
        when(filter.test(baz, foo)).thenReturn(expected);
        assertThat(fixture.test(baz, bar)).isEqualTo(expected);
    }

    @Test
    void test_WhenTheTileHasNoUnit_ThenFalse() {
        when(bar.tile()).thenReturn(Optional.empty());
        assertThat(fixture.test(baz, bar)).isFalse();
        verifyNoInteractions(filter);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("tile.filter");
    }

}
