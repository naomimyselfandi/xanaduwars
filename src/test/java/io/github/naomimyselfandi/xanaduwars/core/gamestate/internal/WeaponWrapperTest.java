package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.MaxRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.MinRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class WeaponWrapperTest {

    @Mock
    private GameState gameState;

    @Mock
    private Unit unit;

    @Mock
    private Weapon weapon;

    @InjectMocks
    private WeaponWrapper fixture;

    @Test
    void getTargets(SeededRng random) {
        when(unit.getGameState()).thenReturn(gameState);
        var targetSpec = random.<TargetSpec>get();
        when(weapon.getTargets()).thenReturn(List.of(targetSpec));
        var minRange = random.nextInt();
        var maxRange = random.nextInt();
        when(gameState.evaluate(new MinRangeQuery(unit, weapon, targetSpec))).thenReturn(minRange);
        when(gameState.evaluate(new MaxRangeQuery(unit, weapon, targetSpec))).thenReturn(maxRange);
        assertThat(fixture.getTargets()).containsExactly(targetSpec.withMinRange(minRange).withMaxRange(maxRange));
    }

}
