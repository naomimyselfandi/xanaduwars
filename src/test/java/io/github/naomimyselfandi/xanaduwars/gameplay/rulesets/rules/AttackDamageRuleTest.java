package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackDamageRuleTest {

    @Mock
    private Unit attacker, defender;

    @InjectMocks
    private AttackDamageRule fixture;

    @EnumSource
    @ParameterizedTest
    void handles(AttackStage stage, SeededRng random) {
        var event = new AttackEvent(attacker, defender, stage, random.nextScalar(), random.nextPercent());
        assertThat(fixture.handles(event, None.NONE)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0.5,0.2,0.3,MAIN
            0.5,0.2,0.3,COUNTER
            0.3,0.4,0.0,MAIN
            0.3,0.4,0.0,COUNTER
            """)
    void handle(double initialHp, double damage, double finalHp, AttackStage stage, SeededRng random) {
        when(defender.hp()).thenReturn(Percent.withDoubleValue(initialHp));
        var event = new AttackEvent(attacker, defender, stage, Percent.withDoubleValue(damage), random.nextPercent());
        assertThat(fixture.handle(event, None.NONE)).isEqualTo(None.NONE);
        verify(defender).hp(Percent.withDoubleValue(finalHp));
    }

}
