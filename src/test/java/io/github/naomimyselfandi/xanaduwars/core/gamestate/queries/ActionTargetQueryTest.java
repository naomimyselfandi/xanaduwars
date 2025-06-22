package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets.TargetListValidator;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ActionTargetQueryTest {

    @Mock
    private Unit subject;

    @Mock
    private NormalAction action;

    private List<Object> targets;

    @Mock
    private TargetListValidator validator;

    @BeforeEach
    void setup() {
        targets = List.of(new Object(), new Object());
    }

    @Test
    void defaultValue(SeededRng random) {
        var result = random.<Result>get();
        when(validator.test(subject, action, targets)).thenReturn(result);
        assertThat(new ActionTargetQuery(subject, action, targets, validator).defaultValue()).isEqualTo(result);
    }

    @Test
    void constructor() {
        var expected = new ActionTargetQuery(subject, action, targets, TargetListValidator.INSTANCE);
        assertThat(new ActionTargetQuery(subject, action, targets)).isEqualTo(expected);
    }

}
