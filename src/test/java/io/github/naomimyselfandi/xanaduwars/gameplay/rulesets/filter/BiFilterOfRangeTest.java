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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiFilterOfRangeTest {

    @Mock
    private Unit foo, bar;

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
        when(foo.distance(bar)).thenReturn(Optional.of(distance));
        assertThat(new BiFilterOfRange<>(range).test(bar, foo)).isEqualTo(range.test(distance));
    }

    @Test
    void test_WhenTheDistanceIsUndefined_ThenFalse() {
        when(foo.distance(bar)).thenReturn(Optional.empty());
        assertThat(new BiFilterOfRange<>(new Range(0, Integer.MAX_VALUE)).test(bar, foo)).isFalse();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0
            0,1
            1,1
            1,2
            2,2
            """)
    void testToString(int min, int max) {
        var range = new Range(min, max);
        assertThat(new BiFilterOfRange<>(range)).hasToString("range(%s)", range);
    }

}
