package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    private Ruleset.Details details;

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
        when(ruleset.details()).thenReturn(details);
        when(details.attackAction()).thenReturn(attackAction);
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

    @ParameterizedTest
    @CsvSource(textBlock = """
            0.00,false
            0.01,true
            0.25,true
            0.50,true
            0.75,true
            1.00,true
            """)
    void defaultValue(double hp, boolean expected) {
        when(counterattacker.hp()).thenReturn(Percent.withDoubleValue(hp));
        assertThat(fixture.defaultValue()).isEqualTo(expected);
    }

}
