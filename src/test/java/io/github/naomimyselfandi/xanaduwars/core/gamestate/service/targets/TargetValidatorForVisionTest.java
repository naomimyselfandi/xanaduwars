package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetValidatorForVisionTest {

    @Mock
    private NormalAction action;

    @Mock
    private Player player;

    @Mock
    private Unit actor, target;

    @InjectMocks
    private TargetValidatorForVision fixture;

    @Test
    void fail() {
        assertThat(fixture.fail()).isEqualTo(Result.fail("Lost vision of target."));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(@Nullable Boolean canSee, SeededRng random) {
        if (canSee != null) {
            when(actor.getOwner()).thenReturn(Optional.of(player));
            when(player.canSee(target)).thenReturn(canSee);
        }
        assertThat(fixture.test(actor, action, target, random.get())).isEqualTo(Boolean.TRUE.equals(canSee));
    }

}
