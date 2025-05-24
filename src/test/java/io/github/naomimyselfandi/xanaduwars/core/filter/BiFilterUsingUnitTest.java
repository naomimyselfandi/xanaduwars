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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiFilterUsingUnitTest {

    @Mock
    private Unit foo;

    @Mock
    private Tile bar, baz;

    @Mock
    private BiFilter<Tile, Unit> filter;

    @InjectMocks
    private BiFilterUsingUnit<Tile> fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected) {
        when(bar.unit()).thenReturn(Optional.of(foo));
        when(filter.test(baz, foo)).thenReturn(expected);
        assertThat(fixture.test(baz, bar)).isEqualTo(expected);
    }

    @Test
    void test_WhenTheUnitHasNoTile_ThenFalse() {
        when(bar.unit()).thenReturn(Optional.empty());
        assertThat(fixture.test(baz, bar)).isFalse();
        verifyNoInteractions(filter);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("unit.filter");
    }

}
