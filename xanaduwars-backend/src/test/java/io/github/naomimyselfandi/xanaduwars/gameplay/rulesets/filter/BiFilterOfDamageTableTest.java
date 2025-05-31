package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BiFilterOfDamageTableTest {

    @Mock
    private UnitType unitType, anotherUnitType;

    @Mock
    private Unit foo, bar;

    private final BiFilterOfDamageTable<Unit> fixture = new BiFilterOfDamageTable<>();

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected, SeededRng random) {
        when(foo.damageTable()).thenReturn(Map.of(unitType, random.nextPercent()));
        when(bar.type()).thenReturn(expected ? unitType : anotherUnitType);
        assertThat(fixture.test(foo, bar)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("hasDamageValue");
    }

}
