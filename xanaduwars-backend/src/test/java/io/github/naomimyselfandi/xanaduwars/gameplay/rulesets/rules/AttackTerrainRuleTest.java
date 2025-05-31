package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Scalar;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AttackTerrainRuleTest {

    @Mock
    private Unit attacker, defender;

    @Mock
    private Tile tile;

    @InjectMocks
    private AttackTerrainRule fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,MAIN
            false,MAIN
            true,COUNTER
            false,COUNTER
            """)
    void handles(boolean expected, AttackStage stage, SeededRandom random) {
        when(defender.tile()).thenReturn(Optional.ofNullable(expected ? tile : null));
        var query = new AttackCalculation(attacker, defender, stage);
        assertThat(fixture.handles(query, Scalar.withDoubleValue(random.nextDouble()))).isEqualTo(expected);
    }

    @EnumSource
    @ParameterizedTest
    void handles_WhenTheDefenderIsATile_ThenFalse(AttackStage stage, SeededRandom random) {
        var query = new AttackCalculation(attacker, tile, stage);
        assertThat(fixture.handles(query, Scalar.withDoubleValue(random.nextDouble()))).isFalse();
    }

    @RepeatedTest(3)
    void handle(SeededRandom random) {
        var base = random.nextDouble();
        var cover = random.nextDouble();
        when(defender.tile()).thenReturn(Optional.of(tile));
        when(tile.cover()).thenReturn(Percent.withDoubleValue(cover));
        var query = new AttackCalculation(attacker, defender, random.pick(AttackStage.values()));
        assertThat(fixture.handle(query, Scalar.withDoubleValue(base))).isEqualTo(Scalar.withDoubleValue(base * cover));
    }

}
