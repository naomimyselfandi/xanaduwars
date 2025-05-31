package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BiFilterOfVisionRangeTest {

    @Mock
    private Unit subject, target;

    private final BiFilterOfVisionRange<Unit> fixture = new BiFilterOfVisionRange<>();

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,3,true
            3,3,true
            3,2,false
            ,3,false
            """)
    void test(@Nullable Integer distance, int vision, boolean expected) {
        when(subject.distance(target)).thenReturn(Optional.ofNullable(distance));
        when(subject.vision()).thenReturn(vision);
        assertThat(fixture.test(subject, target)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("range(vision)");
    }

}
