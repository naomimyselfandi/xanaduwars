package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounterattackValidationTest {

    @Mock
    private Action<Unit, Node> attackAction;

    @Mock
    private Ruleset ruleset;

    @Mock
    private GameState gameState;

    @Mock
    private Unit initiator, counterattacker;

    private CounterattackValidation fixture;

    @BeforeEach
    void setup() {
        fixture = new CounterattackValidation(initiator, counterattacker);
    }

    @Test
    void action() {
        when(initiator.gameState()).thenReturn(gameState);
        when(gameState.ruleset()).thenReturn(ruleset);
        when(ruleset.attackAction()).thenReturn(attackAction);
        assertThat(fixture.action()).isEqualTo(attackAction);
    }

    @Test
    void target() {
        assertThat(fixture.target()).isEqualTo(initiator);
    }

    @Test
    void subject() {
        assertThat(fixture.subject()).isEqualTo(counterattacker);
    }

}
