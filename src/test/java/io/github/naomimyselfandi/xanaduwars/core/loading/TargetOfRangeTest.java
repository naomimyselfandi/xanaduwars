package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetOfRangeTest {

    @Mock
    private Unit actor, target;

    @Mock
    private Target<Unit, Unit> base;

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,2,3,true
            2,2,3,true
            1,3,3,true
            2,1,3,false
            1,3,2,false
            """)
    void validateFurther(int min, double distance, int max, boolean expected) {
        when(actor.getDistance(target)).thenReturn(distance);
        assertThat(new TargetOfRange<>(base, min, max).validateFurther(actor, target)).isEqualTo(expected);
    }

}
