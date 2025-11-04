package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitTagTest {

    @Mock
    private Unit unit;

    @Mock
    private UnitType unitType;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_Unit(boolean value, SeededRng random) {
        var foo = random.<UnitTag>get();
        var bar = value ? foo : random.not(foo);
        var baz = random.not(foo, bar);
        when(unit.getTags()).thenReturn(List.of(bar, baz));
        assertThat(foo.test(unit)).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_UnitType(boolean value, SeededRng random) {
        var foo = random.<UnitTag>get();
        var bar = value ? foo : random.not(foo);
        var baz = random.not(foo, bar);
        when(unitType.getTags()).thenReturn(List.of(bar, baz));
        assertThat(foo.test(unitType)).isEqualTo(value);
    }

}
