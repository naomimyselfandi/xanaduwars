package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiFilterAllTest {

    @Mock
    private Unit foo, bar;

    @Mock
    private BiFilter<Unit, Unit> firstFilter, secondFilter;

    private BiFilterAll<Unit, Unit> fixture;

    @BeforeEach
    void setup() {
        fixture = new BiFilterAll<>(List.of(firstFilter, secondFilter));
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void test(boolean firstResult, boolean secondResult, boolean expected) {
        lenient().when(firstFilter.test(bar, foo)).thenReturn(firstResult);
        lenient().when(secondFilter.test(bar, foo)).thenReturn(secondResult);
        assertThat(fixture.test(bar, foo)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("all(firstFilter, secondFilter)");
    }

}
