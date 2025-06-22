package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ActionGroupQueryTest {

    @Mock
    private Unit actor;

    @Mock
    private NormalAction action0, action1, action2, action3;

    private ActionGroupQuery fixture;

    @BeforeEach
    void setup() {
        fixture = new ActionGroupQuery(actor, List.of(action0, action1));
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void defaultValue(boolean action0Matches, boolean action1Matches, boolean ok) {
        when(actor.getActions()).thenReturn(List.of(
                action0Matches ? action0 : action2,
                action1Matches ? action1 : action3
        ));
        assertThat(fixture.defaultValue() instanceof Result.Okay).isEqualTo(ok);
    }

}
