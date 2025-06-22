package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetListValidatorImplTest {

    @Mock
    private Unit actor;

    @Mock
    private NormalAction action;

    private List<Object> targets;

    private TargetSpec spec0, spec1;

    @Mock
    private TargetValidator<Element, Action, Object> validator0, validator1;

    private TargetListValidatorImpl fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        targets = List.of(new Object(), new Object());
        spec0 = random.get();
        spec1 = random.get();
        when(action.getTargets()).thenReturn(List.of(spec0, spec1));
        fixture = new TargetListValidatorImpl(List.of(validator0, validator1));
        this.random = random;
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true,true,true,
            false,true,true,true,0
            true,false,true,true,1
            true,true,false,true,0
            true,true,true,false,1
            """)
    void test(boolean a, boolean b, boolean c, boolean d, @Nullable Integer failure) {
        var f0 = random.<Result.Fail>get();
        var f1 = random.<Result.Fail>get();
        var result = failure == null ? Result.okay() : List.of(f0, f1).get(failure);
        when(validator0.test(actor, action, targets.getFirst(), spec0)).thenReturn(a);
        when(validator0.fail()).thenReturn(f0);
        when(validator1.test(actor, action, targets.getFirst(), spec0)).thenReturn(b);
        when(validator1.fail()).thenReturn(f1);
        when(validator0.test(actor, action, targets.getLast(), spec1)).thenReturn(c);
        when(validator1.fail()).thenReturn(f1);
        when(validator1.test(actor, action, targets.getLast(), spec1)).thenReturn(d);
        assertThat(fixture.test(actor, action, targets)).isEqualTo(result);
    }

}
