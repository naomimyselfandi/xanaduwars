package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Scalar;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackHpRuleTest {

    @Mock
    private Unit attacker, defender;

    @InjectMocks
    private AttackHpRule fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            0.000,true
            0.500,true
            0.999,true
            1.000,false
            """)
    void handles(double hp, boolean expected, SeededRandom random) {
        when(attacker.hp()).thenReturn(Percent.withDoubleValue(hp));
        var query = new AttackCalculation(attacker, defender, random.pick(AttackStage.values()));
        var value = Scalar.withDoubleValue(random.nextDouble());
        assertThat(fixture.handles(query, value)).isEqualTo(expected);
    }

    @RepeatedTest(3)
    void handle(SeededRandom random) {
        var base = random.nextDouble();
        var hp = random.nextDouble();
        when(attacker.hp()).thenReturn(Percent.withDoubleValue(hp));
        var query = new AttackCalculation(attacker, defender, random.pick(AttackStage.values()));
        var value = Scalar.withDoubleValue(base);
        assertThat(fixture.handle(query, value)).isEqualTo(Scalar.withDoubleValue(base * hp));
    }

}
