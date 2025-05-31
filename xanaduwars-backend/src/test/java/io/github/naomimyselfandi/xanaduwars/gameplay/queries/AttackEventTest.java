package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Scalar;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackEventTest {

    @Mock
    private GameState gameState;

    @Mock
    private Unit attacker;

    @Mock
    private Unit target;

    @ParameterizedTest
    @CsvSource(textBlock = """
            0.2,0.8,0.2,MAIN
            0.2,0.8,0.2,COUNTER
            0.5,0.5,0.5,MAIN
            0.5,0.5,0.5,COUNTER
            0.5,0.1,0.1,MAIN
            0.5,0.1,0.1,COUNTER
            """)
    void of(double damage, double initialHp, double actualDamage, AttackStage stage) {
        var damageScalar = Scalar.withDoubleValue(damage);
        var initialHpPct = Percent.withDoubleValue(initialHp);
        var actualHpPct = Percent.withDoubleValue(actualDamage);
        of(damageScalar, initialHpPct, actualHpPct, stage);
    }

    private void of(Scalar damage, Percent initialHp, Percent actualDamage, AttackStage stage) {
        when(target.hp()).thenReturn(initialHp);
        when(attacker.gameState()).thenReturn(gameState);
        when(gameState.evaluate(new AttackCalculation(attacker, target, stage))).thenReturn(damage);
        var expected = new AttackEvent(attacker, target, stage, damage, actualDamage);
        assertThat(AttackEvent.of(attacker, target, stage)).isEqualTo(expected);
    }

}
