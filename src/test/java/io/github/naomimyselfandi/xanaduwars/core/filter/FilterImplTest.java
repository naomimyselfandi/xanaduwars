package io.github.naomimyselfandi.xanaduwars.core.filter;

import io.github.naomimyselfandi.xanaduwars.core.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterImplTest {

    @Mock
    private Unit unit;

    @Mock
    private BiFilter<Unit, Unit> filter;

    @InjectMocks
    private FilterImpl<Unit> fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected) {
        when(filter.test(unit, unit)).thenReturn(expected);
        assertThat(fixture.test(unit)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("filter");
    }

}
