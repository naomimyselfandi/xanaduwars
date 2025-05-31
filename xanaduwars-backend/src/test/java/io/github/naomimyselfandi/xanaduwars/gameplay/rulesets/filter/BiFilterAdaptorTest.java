package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiFilterAdaptorTest {

    @Mock
    private Unit foo, bar;

    @Mock
    private Tile tile;

    @Mock
    private BiFilter<Unit, Unit> filter;

    private BiFilterAdaptor<Unit, Node, Unit> fixture;

    @BeforeEach
    void setup() {
        fixture = new BiFilterAdaptor<>(Unit.class, filter);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected) {
        when(filter.test(bar, foo)).thenReturn(expected);
        assertThat(fixture.test(bar, foo)).isEqualTo(expected);
    }

    @Test
    void test_WhenTheInputIsTheWrongType_ThenFalse() {
        assertThat(fixture.test(bar, tile)).isFalse();
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("as(Unit).filter");
    }

}
