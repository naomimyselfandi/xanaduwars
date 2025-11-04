package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitSelectorMapTest {

    @Test
    void empty() {
        assertThat(UnitSelectorMap.<String>empty()).isInstanceOf(UnitSelectorMapImpl.class).isEmpty();
    }

    @Test
    void copyOf(SeededRng random) {
        var foo = random.<UnitSelector>get();
        var fooValue = random.nextString();
        var bar = random.not(foo);
        var barValue = random.not(fooValue);
        var map = Map.of(foo, fooValue, bar, barValue);
        assertThat(UnitSelectorMap.copyOf(map)).isInstanceOf(UnitSelectorMapImpl.class).isEqualTo(map);
    }

}
