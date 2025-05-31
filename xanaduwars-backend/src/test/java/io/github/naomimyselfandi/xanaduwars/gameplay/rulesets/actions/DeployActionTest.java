package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitCostCalculation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class DeployActionTest {

    @Mock
    private Ruleset ruleset;

    @Mock
    private GameState gameState;

    @Mock
    private UnitType unitType, anotherUnitType;

    @Mock
    private Player player;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit;

    private DeployAction fixture;

    @BeforeEach
    void setup(SeededRng random) {
        lenient().when(tile.gameState()).thenReturn(gameState);
        fixture = new DeployAction(random.nextName());
    }

    @Test
    void enumerateTargets() {
        when(gameState.ruleset()).thenReturn(ruleset);
        when(ruleset.unitTypes()).thenReturn(List.of(unitType, anotherUnitType));
        assertThat(fixture.enumerateTargets(gameState)).containsExactly(unitType, anotherUnitType);
    }

    @Test
    void execute() {
        when(tile.owner()).thenReturn(Optional.of(player));
        assertThat(fixture.execute(tile, unitType)).isEqualTo(Execution.SUCCESSFUL);
        verify(tile).createUnit(unitType, player);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean ok) {
        when(tile.deploymentRoster()).thenReturn(Set.of(ok ? unitType : anotherUnitType));
        assertThat(fixture.test(tile, unitType)).isEqualTo(ok);
    }

    @Test
    void test_WhenTheTileHasAUnit_ThenFalse() {
        lenient().when(tile.deploymentRoster()).thenReturn(Set.of(unitType));
        when(tile.unit()).thenReturn(Optional.of(unit));
        assertThat(fixture.test(tile, unitType)).isFalse();
    }

    @EnumSource
    @ParameterizedTest
    void cost(Resource resource, SeededRng random) {
        var cost = random.nextIntNotNegative();
        var calculation = new UnitCostCalculation(tile, unitType, resource);
        when(gameState.evaluate(calculation)).thenReturn(cost);
        assertThat(fixture.cost(resource, tile, unitType)).isEqualTo(cost);
    }

}
