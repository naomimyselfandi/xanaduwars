package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class IsNodePerceivedByUnitQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Unit target, observer;

    private IsNodePerceivedByUnitQuery fixture;

    @BeforeEach
    void setup() {
        fixture = new IsNodePerceivedByUnitQuery(target, observer);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,3,true
            3,3,true
            3,2,false
            NaN,2,false
            """)
    void defaultValue(double distance, int perception, boolean expected) {
        when(observer.getDistance(target)).thenReturn(distance);
        when(observer.getPerception()).thenReturn(perception);
        assertThat(fixture.defaultValue(runtime)).isEqualTo(expected);
        verifyNoInteractions(runtime);
    }

}
