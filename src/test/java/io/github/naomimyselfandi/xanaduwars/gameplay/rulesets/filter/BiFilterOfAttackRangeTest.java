package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Range;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiFilterOfAttackRangeTest {

    @Mock
    private Unit foo, bar;

    private final BiFilterOfAttackRange<Unit> fixture = new BiFilterOfAttackRange<>();

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,0
            0,1,0
            0,0,1
            1,1,1
            1,2,0
            1,2,1
            1,2,2
            1,2,3
            """)
    void test(int min, int max, int distance) {
        var range = new Range(min, max);
        when(foo.range()).thenReturn(range);
        when(foo.distance(bar)).thenReturn(Optional.of(distance));
        assertThat(fixture.test(foo, bar)).isEqualTo(range.test(distance));
    }

    @Test
    void test_WhenTheDistanceIsUndefined_ThenFalse() {
        var range = new Range(0, Integer.MAX_VALUE);
        when(foo.range()).thenReturn(range);
        when(foo.distance(bar)).thenReturn(Optional.empty());
        assertThat(fixture.test(foo, bar)).isFalse();
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("range(attack)");
    }

}
