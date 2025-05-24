package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Execution;
import io.github.naomimyselfandi.xanaduwars.core.GameState;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.core.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.core.queries.AttackStage;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackActionTest {

    @Mock
    private GameState gameState;

    @Mock
    private UnitType targetType;

    @Mock
    private Unit attacker, target;

    private AttackAction fixture;

    @BeforeEach
    void setup(SeededRandom random) {
        when(attacker.gameState()).thenReturn(gameState);
        when(target.type()).thenReturn(targetType);
        var name = new Name("A" + random.nextInt(Integer.MAX_VALUE));
        fixture = new AttackAction(name, TagSet.EMPTY);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1.0,0.2,0.8
            0.2,0.3,0.0
            """)
    void execute(double initialHp, double damage, double finalHp, SeededRandom random) {
        var baseDamage = Percent.withDoubleValue(random.nextDouble());
        var calculation = new AttackCalculation(attacker, target, AttackStage.MAIN);
        var event = new AttackEvent(attacker, target, AttackStage.MAIN, Percent.withDoubleValue(damage));
        when(attacker.damageTable()).thenReturn(Map.of(targetType, baseDamage));
        when(gameState.evaluate(calculation, baseDamage)).thenReturn(Percent.withDoubleValue(damage));
        when(target.hp()).thenReturn(Percent.withDoubleValue(initialHp));
        assertThat(fixture.execute(attacker, target)).isEqualTo(Execution.SUCCESSFUL);
        var inOrder = inOrder(target, gameState);
        inOrder.verify(target).hp(Percent.withDoubleValue(finalHp));
        inOrder.verify(gameState).evaluate(event);
    }

}
