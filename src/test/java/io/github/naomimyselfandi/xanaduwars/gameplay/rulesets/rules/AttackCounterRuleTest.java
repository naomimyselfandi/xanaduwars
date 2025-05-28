package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.CounterattackValidation;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static io.github.naomimyselfandi.xanaduwars.ext.None.NONE;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackCounterRuleTest {

    @Mock
    private GameState gameState;

    @Mock
    private Unit attacker, defender;

    @Mock
    private Tile tile;

    @InjectMocks
    private AttackCounterRule fixture;

    @BeforeEach
    void setup() {
        when(attacker.gameState()).thenReturn(gameState);
        when(defender.gameState()).thenReturn(gameState);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            MAIN,true,true,true
            COUNTER,true,true,false
            MAIN,false,true,false
            COUNTER,false,true,false
            MAIN,true,false,false
            COUNTER,true,false,false
            """)
    void handles(AttackStage stage, boolean defenderIsUnit, boolean canCounter, boolean expected, SeededRng random) {
        var query = new AttackEvent(
                attacker,
                defenderIsUnit ? defender : tile,
                stage,
                random.nextScalar(),
                random.nextPercent()
        );
        when(gameState.evaluate(new CounterattackValidation(attacker, defender))).thenReturn(canCounter);
        assertThat(fixture.handles(query, None.NONE)).isEqualTo(expected);
    }

    @Test
    void handle(SeededRng random) {
        when(gameState.evaluate(any(AttackCalculation.class))).thenReturn(random.nextScalar());
        when(attacker.hp()).thenReturn(random.nextPercent());
        when(defender.hp()).thenReturn(random.nextPercent());
        assertThat(fixture.handle(AttackEvent.of(attacker, defender, AttackStage.MAIN), NONE)).isEqualTo(NONE);
        var event = AttackEvent.of(defender, attacker, AttackStage.COUNTER);
        verify(gameState).evaluate(event);
    }

}
