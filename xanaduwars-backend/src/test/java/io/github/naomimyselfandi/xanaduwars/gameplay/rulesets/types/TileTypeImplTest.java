package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.TileType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TileTypeImplTest {

    @Mock
    private TileType foundation;

    @ParameterizedTest
    @CsvSource(textBlock = """
            false,0,true
            true,1,true
            true,2,true
            true,0,false
            false,1,false
            false,2,false
            """)
    void isConsistent(boolean hasFoundation, int buildTime, boolean expected, SeededRng random) {
        var instance = new TileTypeImpl(random.nextTileTypeId(), random.nextName()).buildTime(buildTime);
        if (hasFoundation) instance.foundation(foundation);
        assertThat(instance.isConsistent()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void foundation(boolean hasFoundation, SeededRng random) {
        var instance = new TileTypeImpl(random.nextTileTypeId(), random.nextName());
        if (hasFoundation) instance.foundation(foundation);
        assertThat(instance.foundation()).isEqualTo(Optional.of(foundation).filter(_ -> hasFoundation));
    }

    @Test
    void testToString(SeededRng random) {
        var instance = new TileTypeImpl(random.nextTileTypeId(), random.nextName());
        assertThat(instance).hasToString("%s(%s)", instance.id(), instance.name());
    }

}
