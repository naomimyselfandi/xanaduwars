package io.github.naomimyselfandi.xanaduwars.core.filter;

import io.github.naomimyselfandi.xanaduwars.core.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class BiFilterAnyTest {

    @Mock
    private Unit foo, bar;

    @Mock
    private BiFilter<Unit, Unit> firstFilter, secondFilter;

    private BiFilterAny<Unit, Unit> fixture;

    @BeforeEach
    void setup() {
        fixture = new BiFilterAny<>(List.of(firstFilter, secondFilter));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true,true
            true,false,true
            false,true,true
            false,false,false
            """)
    void test(boolean firstResult, boolean secondResult, boolean expected) {
        lenient().when(firstFilter.test(bar, foo)).thenReturn(firstResult);
        lenient().when(secondFilter.test(bar, foo)).thenReturn(secondResult);
        assertThat(fixture.test(bar, foo)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("any(firstFilter, secondFilter)");
    }

}
