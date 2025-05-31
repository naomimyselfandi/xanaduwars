package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.AttackStage;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackActionTest {

    @Mock
    private GameState gameState;

    @Mock
    private Unit attacker, defender;

    @Mock
    private BiFilter<Unit, Node> filter;

    private AttackAction fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new AttackAction(random.nextName(), filter);
    }

    @Test
    void execute(SeededRng random) {
        when(gameState.evaluate(any(AttackCalculation.class))).thenReturn(random.nextScalar());
        when(attacker.gameState()).thenReturn(gameState);
        when(defender.hp()).thenReturn(random.nextPercent());
        var event = AttackEvent.of(attacker, defender, AttackStage.MAIN);
        assertThat(fixture.execute(attacker, defender)).isEqualTo(Execution.SUCCESSFUL);
        verify(gameState).evaluate(event);
    }

}
