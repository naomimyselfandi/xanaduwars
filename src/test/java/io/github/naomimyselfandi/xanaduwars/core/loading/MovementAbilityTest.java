package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.messages.CanBoardQuery;
import io.github.naomimyselfandi.xanaduwars.core.messages.CanPassOverQuery;
import io.github.naomimyselfandi.xanaduwars.core.messages.GetMovementCostQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class MovementAbilityTest {

    @Mock
    private GameState gameState;

    @Mock
    private Player player;

    @Mock
    private Unit unit, anotherUnit;

    @Mock
    private Tile tile1, tile2, tile3;

    private MovementAbility fixture;

    @BeforeEach
    void setup() {
        lenient().when(unit.getGameState()).thenReturn(gameState);
        lenient().when(unit.getOwner()).thenReturn(player);
        fixture = new MovementAbility();
    }

    @Test
    void getCost() {
        var free = new Ability.Cost(0, 0, 0);
        assertThat(fixture.getCost(unit, new Tile[]{})).isEqualTo(free);
        assertThat(fixture.getCost(unit, new Tile[]{tile1})).isEqualTo(free);
        assertThat(fixture.getCost(unit, new Tile[]{tile1, tile2})).isEqualTo(free);
        assertThat(fixture.getCost(unit, new Tile[]{tile1, tile2, tile3})).isEqualTo(free);
    }

    @Test
    void isSpellChoice() {
        assertThat(fixture.isSpellChoice()).isFalse();
    }

    @Test
    void getCapacity(SeededRng random) {
        var value = random.nextInt();
        when(unit.getSpeed()).thenReturn(value);
        assertThat(fixture.getCapacity(unit)).isEqualTo(value);
    }

    @Test
    void getCapacityUsed(SeededRng random) {
        var cost1 = random.nextDouble();
        var cost2 = random.nextDouble();
        var cost3 = random.nextDouble();
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile1))).thenReturn(cost1);
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile2))).thenReturn(cost2);
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile3))).thenReturn(cost3);
        assertThat(fixture.getCapacityUsed(unit, List.of())).isZero();
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1))).isEqualTo(cost1);
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1, tile2))).isEqualTo(cost1 + cost2);
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1, tile2, tile3))).isEqualTo(cost1 + cost2 + cost3);
    }

    @Test
    void getCapacityUsed_WhenThePathContainsANonObstacleUnit_ThenIgnoresIt(SeededRng random) {
        var cost1 = random.nextDouble();
        var cost2 = random.nextDouble();
        var cost3 = random.nextDouble();
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile1))).thenReturn(cost1);
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile2))).thenReturn(cost2);
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile3))).thenReturn(cost3);
        when(tile2.getUnit()).thenReturn(anotherUnit);
        when(gameState.evaluate(new CanPassOverQuery(unit, anotherUnit))).thenReturn(true);
        lenient().when(player.perceives(anotherUnit)).thenReturn(true);
        assertThat(fixture.getCapacityUsed(unit, List.of())).isZero();
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1))).isEqualTo(cost1);
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1, tile2))).isEqualTo(cost1 + cost2);
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1, tile2, tile3))).isEqualTo(cost1 + cost2 + cost3);
    }

    @Test
    void getCapacityUsed_WhenThePathContainsAnInvisibleObstacle_ThenIgnoresIt(SeededRng random) {
        var cost1 = random.nextDouble();
        var cost2 = random.nextDouble();
        var cost3 = random.nextDouble();
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile1))).thenReturn(cost1);
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile2))).thenReturn(cost2);
        when(gameState.evaluate(new GetMovementCostQuery(unit, tile3))).thenReturn(cost3);
        when(tile2.getUnit()).thenReturn(anotherUnit);
        lenient().when(gameState.evaluate(new CanPassOverQuery(unit, anotherUnit))).thenReturn(false);
        lenient().when(player.perceives(anotherUnit)).thenReturn(false);
        assertThat(fixture.getCapacityUsed(unit, List.of())).isZero();
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1))).isEqualTo(cost1);
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1, tile2))).isEqualTo(cost1 + cost2);
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1, tile2, tile3))).isEqualTo(cost1 + cost2 + cost3);
    }

    @Test
    void getCapacityUsed_WhenThePathContainsAPerceivedObstacle_ThenInfinity(SeededRng random) {
        var cost1 = random.nextDouble();
        var cost2 = random.nextDouble();
        var cost3 = random.nextDouble();
        lenient().when(gameState.evaluate(new GetMovementCostQuery(unit, tile1))).thenReturn(cost1);
        lenient().when(gameState.evaluate(new GetMovementCostQuery(unit, tile2))).thenReturn(cost2);
        lenient().when(gameState.evaluate(new GetMovementCostQuery(unit, tile3))).thenReturn(cost3);
        when(tile2.getUnit()).thenReturn(anotherUnit);
        when(gameState.evaluate(new CanPassOverQuery(unit, anotherUnit))).thenReturn(false);
        when(player.perceives(anotherUnit)).thenReturn(true);
        assertThat(fixture.getCapacityUsed(unit, List.of(tile1, tile2, tile3))).isInfinite().isPositive();
    }

    @Test
    void validate() {
        assertThat(fixture.validate(unit, List.of())).isFalse();
        assertThat(fixture.validate(unit, List.of(tile1))).isTrue();
        assertThat(fixture.validate(unit, List.of(tile1, tile2))).isTrue();
        assertThat(fixture.validate(unit, List.of(tile1, tile2, tile3))).isTrue();
    }

    @Test
    void validate_WhenAUnitIsOnThePathButNotAtTheEnd_ThenIgnoresIt() {
        lenient().when(tile2.getUnit()).thenReturn(anotherUnit);
        lenient().when(player.perceives(anotherUnit)).thenReturn(true);
        assertThat(fixture.validate(unit, List.of(tile1, tile2, tile3))).isTrue();
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void validate_WhenTheEndHasAUnit_ThenItMustBeATransportOrInvisible(
            boolean transport,
            boolean invisible,
            boolean expected
    ) {
        when(tile3.getUnit()).thenReturn(anotherUnit);
        lenient().when(gameState.evaluate(new CanBoardQuery(unit, anotherUnit))).thenReturn(transport);
        lenient().when(player.perceives(anotherUnit)).thenReturn(!invisible);
        assertThat(fixture.validate(unit, List.of(tile1, tile2, tile3))).isEqualTo(expected);
    }

    @Test
    void execute() {
        assertThat(fixture.execute(unit, List.of(tile1, tile2, tile3))).isTrue();
        var inOrder = inOrder(unit);
        inOrder.verify(unit).setLocation(tile1);
        inOrder.verify(unit).setLocation(tile2);
        inOrder.verify(unit).setLocation(tile3);
    }

    @Test
    void execute_WhenAnObstacleIsOnThePath_ThenSkipsItsTile() {
        when(tile2.getUnit()).thenReturn(anotherUnit);
        when(gameState.evaluate(new CanPassOverQuery(unit, anotherUnit))).thenReturn(true);
        assertThat(fixture.execute(unit, List.of(tile1, tile2, tile3))).isTrue();
        var inOrder = inOrder(unit);
        inOrder.verify(unit).setLocation(tile1);
        inOrder.verify(unit).setLocation(tile3);
        verify(unit, never()).setLocation(tile2);
        verify(unit, never()).setLocation(anotherUnit);
    }

    @Test
    void execute_WhenAnObstacleIsOnThePathAndItCannotBePassedOver_ThenTheUnitGetsStuck() {
        when(tile2.getUnit()).thenReturn(anotherUnit);
        when(gameState.evaluate(new CanPassOverQuery(unit, anotherUnit))).thenReturn(false);
        assertThat(fixture.execute(unit, List.of(tile1, tile2, tile3))).isFalse();
        verify(unit).setLocation(tile1);
        verify(unit, never()).setLocation(tile2);
        verify(unit, never()).setLocation(anotherUnit);
        verify(unit, never()).setLocation(tile3);
    }

    @Test
    void execute_WhenTheLastTileContainsATransport_ThenBoardsIt() {
        when(tile3.getUnit()).thenReturn(anotherUnit);
        when(gameState.evaluate(new CanPassOverQuery(unit, anotherUnit))).thenReturn(true);
        when(gameState.evaluate(new CanBoardQuery(unit, anotherUnit))).thenReturn(true);
        assertThat(fixture.execute(unit, List.of(tile1, tile2, tile3))).isTrue();
        var inOrder = inOrder(unit);
        inOrder.verify(unit).setLocation(tile1);
        inOrder.verify(unit).setLocation(tile2);
        inOrder.verify(unit).setLocation(anotherUnit);
        verify(unit, never()).setLocation(tile3);
    }

    @Test
    void execute_WhenTheLastTileContainsAUnitThatCannotBeBoarded_ThenGetsStuck() {
        when(tile3.getUnit()).thenReturn(anotherUnit);
        when(gameState.evaluate(new CanPassOverQuery(unit, anotherUnit))).thenReturn(true);
        when(gameState.evaluate(new CanBoardQuery(unit, anotherUnit))).thenReturn(false);
        assertThat(fixture.execute(unit, List.of(tile1, tile2, tile3))).isFalse();
        var inOrder = inOrder(unit);
        inOrder.verify(unit).setLocation(tile1);
        inOrder.verify(unit).setLocation(tile2);
        verify(unit, never()).setLocation(anotherUnit);
        verify(unit, never()).setLocation(tile3);
    }

}
