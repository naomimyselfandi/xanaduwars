package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitSelectorMapImplTest {

    @Mock
    private Unit unit;

    @Mock
    private UnitType unitType;

    @Mock
    private UnitSelector foo, bar, baz;

    private int fooValue, barValue, bazValue;

    private Map<UnitSelector, Integer> map;

    private UnitSelectorMapImpl<Integer> fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fooValue = random.nextInt(0, 10);
        barValue = random.nextInt(10, 20);
        bazValue = random.nextInt(20, 30);
        map = new HashMap<>(Map.of(foo, fooValue, bar, barValue, baz, bazValue));
        fixture = new UnitSelectorMapImpl<>(map);
    }

    @Test
    void min_Unit() {
        assertThat(fixture.min(unit)).isNull();
        when(baz.test(unit)).thenReturn(true);
        assertThat(fixture.min(unit)).isEqualTo(bazValue);
        when(bar.test(unit)).thenReturn(true);
        assertThat(fixture.min(unit)).isEqualTo(barValue);
        when(foo.test(unit)).thenReturn(true);
        assertThat(fixture.min(unit)).isEqualTo(fooValue);
    }

    @Test
    void min_UnitType() {
        assertThat(fixture.min(unitType)).isNull();
        when(baz.test(unitType)).thenReturn(true);
        assertThat(fixture.min(unitType)).isEqualTo(bazValue);
        when(bar.test(unitType)).thenReturn(true);
        assertThat(fixture.min(unitType)).isEqualTo(barValue);
        when(foo.test(unitType)).thenReturn(true);
        assertThat(fixture.min(unitType)).isEqualTo(fooValue);
    }

    @Test
    void max_Unit() {
        assertThat(fixture.max(unit)).isNull();
        when(foo.test(unit)).thenReturn(true);
        assertThat(fixture.max(unit)).isEqualTo(fooValue);
        when(bar.test(unit)).thenReturn(true);
        assertThat(fixture.max(unit)).isEqualTo(barValue);
        when(baz.test(unit)).thenReturn(true);
        assertThat(fixture.max(unit)).isEqualTo(bazValue);
    }

    @Test
    void max_UnitType() {
        assertThat(fixture.max(unitType)).isNull();
        when(foo.test(unitType)).thenReturn(true);
        assertThat(fixture.max(unitType)).isEqualTo(fooValue);
        when(bar.test(unitType)).thenReturn(true);
        assertThat(fixture.max(unitType)).isEqualTo(barValue);
        when(baz.test(unitType)).thenReturn(true);
        assertThat(fixture.max(unitType)).isEqualTo(bazValue);
    }

    @Test
    void testEquals() {
        assertThat(fixture).isEqualTo(map);
        assertThat(fixture).isNotEqualTo(Map.of(foo, fooValue, bar, barValue));
        assertThat(fixture).isNotEqualTo(Map.of(foo, fooValue, bar, bazValue, baz, barValue));
    }

    @Test
    void testHashCode() {
        assertThat(fixture).hasSameHashCodeAs(map);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString(Map.copyOf(map).toString());
    }

    @Test
    void constructor() {
        assertThat(fixture.map()).isUnmodifiable().isEqualTo(Map.of(foo, fooValue, bar, barValue, baz, bazValue));
        map.clear();
        assertThat(fixture.map()).isUnmodifiable().isEqualTo(Map.of(foo, fooValue, bar, barValue, baz, bazValue));
    }

}
