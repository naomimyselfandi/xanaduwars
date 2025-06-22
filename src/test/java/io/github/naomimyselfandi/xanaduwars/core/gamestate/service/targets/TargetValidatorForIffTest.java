package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.Iff;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetValidatorForIffTest {

    @Mock
    private NormalAction action;

    @Mock
    private Unit actor, target;

    @InjectMocks
    private TargetValidatorForIff fixture;

    @Test
    void fail() {
        assertThat(fixture.fail()).isEqualTo(Result.fail("Target is on wrong team."));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            OWN,true
            OWN,false
            ALLY,true
            ALLY,false
            ENEMY,true
            ENEMY,false
            NEUTRAL,true
            NEUTRAL,false
            """)
    void test(Iff iff, boolean ok, SeededRng random) {
        var _ = switch (iff) {
            case OWN -> when(actor.hasSameOwner(target)).thenReturn(true);
            case ALLY -> when(actor.isAlly(target)).thenReturn(true);
            case ENEMY -> when(actor.isEnemy(target)).thenReturn(true);
            case NEUTRAL -> null;
        };
        var spec = new TargetSpec(Map.of(iff, ok), random.get(), random.get(), random.get());
        assertThat(fixture.test(actor, action, target, spec)).isEqualTo(ok);
    }

}
