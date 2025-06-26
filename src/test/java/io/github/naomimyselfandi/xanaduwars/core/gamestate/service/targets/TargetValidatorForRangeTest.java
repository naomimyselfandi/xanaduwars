package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetValidatorForRangeTest {

    @Mock
    private NormalAction action;

    @Mock
    private Unit actor, target;

    @InjectMocks
    private TargetValidatorForRange fixture;

    @Test
    void fail() {
        assertThat(fixture.fail()).isEqualTo(Result.fail("Target is out of range."));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,1,3,true
            2,2,3,true
            2,1,2,true
            1,2,3,false
            4,2,3,false
            NaN,1,3,false
            """)
    void test(double distance, int min, int max, boolean ok, SeededRng random) {
        when(actor.getDistance(target)).thenReturn(distance);
        var spec = random.<TargetSpec>get().withMinRange(min).withMaxRange(max);
        assertThat(fixture.test(actor, action, target, spec)).isEqualTo(ok);
    }

}
