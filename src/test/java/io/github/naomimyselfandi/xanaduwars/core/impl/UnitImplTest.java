package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.xanaduwars.core.messages.*;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitImplTest extends GameStateAwareTest<UnitImpl> {

    @Mock
    private Ability ability;

    @Mock
    private UnitType type, anotherType;

    @Mock
    private Player player1, player2, player3, player4;

    @Mock
    private Tile tile, anotherTile;

    @Mock
    private Unit unit;

    @BeforeEach
    void setupPlayers() {
        fixture.setOwner(player1);
    }

    @Test
    void getTags() {
        var foo = random.<UnitTag>get();
        var bar = random.not(foo);
        when(type.getTags()).thenReturn(List.of(foo, bar));
        assertThat(fixture.getTags()).containsExactly(foo, bar);
    }

    @Test
    void setType() {
        testSetter(Unit::getType, Unit::setType, type, anotherType, new GenericEvent(fixture));
    }

    @Test
    void asPlayer() {
        assertThat(fixture.asPlayer()).isEqualTo(player1);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isReady(boolean value) {
        when(gameState.call("isReady", fixture)).thenReturn(value);
        initializeFixture();
        assertThat(fixture.isReady()).isEqualTo(value);
    }

    @Test
    void setOwner() {
        testSetter(Unit::getOwner, Unit::setOwner, player1, player2, new GenericEvent(fixture));
        assertThat(fixture.asPlayer()).isEqualTo(player2);
    }

    @Test
    void setLocation() {
        assertThat(fixture.setLocation(tile)).isSameAs(fixture);
        testSetter(Unit::getLocation, Unit::setLocation, tile, anotherTile, new GenericEvent(fixture));
        verify(tile).setUnit(null);
    }

    @Test
    void getSpeed() {
        testGetter(Unit::getSpeed, new GetSpeedQuery(fixture));
    }

    @Test
    void getPerception() {
        testGetter(Unit::getPerception, new GetPerceptionQuery(fixture));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void perceives(boolean value) {
        when(gameState.evaluate(new IsNodePerceivedByUnitQuery(tile, fixture))).thenReturn(value);
        initializeFixture();
        assertThat(fixture.perceives(tile)).isEqualTo(value);
        initializeFixture();
    }

    @Test
    void getMaxHp() {
        testGetter(Unit::getMaxHp, new GetMaxHpQuery(fixture));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,false
            0.1,true
            0.2,true
            1,true
            -1,false
            """)
    void isAlive(double hpPercent, boolean expected) {
        fixture.setHpPercent(hpPercent);
        assertThat(fixture.isAlive()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            100,10,10,1
            100,100,100,1
            100,1000,100,0.1
            789,1000,789,0.789
            789,10000,789,0.0789
            6789,8000,6789,0.8486
            6789,7000,6789,0.9699
            6789,6000,6000,1
            """)
    void setHp(int hp, int maxHp, int normalized, double percent) {
        when(gameState.evaluate(new GetMaxHpQuery(fixture))).thenReturn(maxHp);
        initializeFixture();
        assertThat(fixture.setHp(hp)).isSameAs(fixture);
        assertThat(fixture.getHp()).isEqualTo(normalized);
        assertThat(fixture.getHpPercent()).isEqualTo(percent);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void setHp_WhenTheValueIsNotPositive_ThenDestroysTheUnit(int value) {
        when(gameState.evaluate(new GetMaxHpQuery(fixture))).thenReturn(1000);
        doAnswer(_ -> {
            assertThat(fixture.getHpPercent()).isZero();
            verify(tile, never()).setUnit(null);
            return null;
        }).when(gameState).dispatch(new UnitDestroyedEvent(fixture));
        fixture.setLocation(tile);
        initializeFixture();
        assertThat(fixture.setHp(value)).isEqualTo(fixture);
        var inOrder = inOrder(gameState, tile);
        inOrder.verify(gameState).dispatch(new UnitDestroyedEvent(fixture));
        inOrder.verify(tile).setUnit(null);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void setHp_WhenTheValueIsNotPositive_ThenDestroysAnyCargo(int value) {
        when(gameState.evaluate(new GetMaxHpQuery(fixture))).thenReturn(1000);
        doAnswer(_ -> {
            assertThat(fixture.getHpPercent()).isZero();
            verify(tile, never()).setUnit(null);
            return null;
        }).when(gameState).dispatch(new UnitDestroyedEvent(fixture));
        fixture.setLocation(tile).setUnit(unit);
        initializeFixture();
        assertThat(fixture.setHp(value)).isEqualTo(fixture);
        var inOrder = inOrder(unit, gameState, tile);
        inOrder.verify(unit).setHpPercent(0);
        inOrder.verify(gameState).dispatch(new UnitDestroyedEvent(fixture));
        inOrder.verify(tile).setUnit(null);
    }

    @RepeatedTest(4)
    void setHpPercent(SeededRng random) {
        var value = random.nextInt(1, 9999) / 10000.0;
        testSetter(Unit::getHpPercent, Unit::setHpPercent, 1.0, value, new GenericEvent(fixture));
    }

    @RepeatedTest(4)
    void setHpPercent_WhenThePercentIsGreaterThanOne_ThenNormalizesItToOne(SeededRng random) {
        var first = random.nextInt(1, 9999) / 10000.0;
        var second = random.nextInt(10001, 19999) / 10000.0;
        assertThat(fixture.setHpPercent(first)).isSameAs(fixture);
        assertThat(fixture.setHpPercent(second)).isSameAs(fixture);
        assertThat(fixture.getHpPercent()).isOne();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -0.1, -1, -10})
    void setHpPercent_WhenTheValueIsNotPositive_ThenDestroysTheUnit(double value) {
        doAnswer(_ -> {
            assertThat(fixture.getHpPercent()).isZero();
            verify(tile, never()).setUnit(null);
            return null;
        }).when(gameState).dispatch(new UnitDestroyedEvent(fixture));
        fixture.setLocation(tile);
        initializeFixture();
        assertThat(fixture.setHpPercent(value)).isEqualTo(fixture);
        var inOrder = inOrder(gameState, tile);
        inOrder.verify(gameState).dispatch(new UnitDestroyedEvent(fixture));
        inOrder.verify(tile).setUnit(null);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -0.1, -1, -10})
    void setHpPercent_WhenTheValueIsNotPositive_ThenDestroysAnyCargo(double value) {
        doAnswer(_ -> {
            assertThat(fixture.getHpPercent()).isZero();
            verify(tile, never()).setUnit(null);
            return null;
        }).when(gameState).dispatch(new UnitDestroyedEvent(fixture));
        fixture.setLocation(tile).setUnit(unit);
        initializeFixture();
        assertThat(fixture.setHpPercent(value)).isEqualTo(fixture);
        var inOrder = inOrder(unit, gameState, tile);
        inOrder.verify(unit).setHpPercent(0);
        inOrder.verify(gameState).dispatch(new UnitDestroyedEvent(fixture));
        inOrder.verify(tile).setUnit(null);
    }

    @Test
    void setUnderConstruction() {
        Getter<UnitImpl, Boolean> getter = Unit::isUnderConstruction;
        Setter<UnitImpl, Boolean> setter = Unit::setUnderConstruction;
        testSetter(getter, setter, false, true, new GenericEvent(fixture));
        clearInvocations(gameState);
        testSetter(getter, setter, true, false, new GenericEvent(fixture));
    }

    @Test
    void getSupplyCost() {
        var cost = random.nextInt();
        when(type.getSupplyCost()).thenReturn(cost);
        assertThat(fixture.getSupplyCost()).isEqualTo(cost);
    }

    @Test
    void getAetherCost() {
        var cost = random.nextInt();
        when(type.getAetherCost()).thenReturn(cost);
        assertThat(fixture.getAetherCost()).isEqualTo(cost);
    }

    @Test
    void getHangar() {
        var foo = random.<UnitTag>get();
        var bar = random.<UnitTag>get();
        when(type.getHangar()).thenReturn(List.of(foo, bar));
        assertThat(fixture.getHangar()).containsExactly(foo, bar);
    }

    @Test
    void getAbilities() {
        testGetter(Unit::getAbilities, new UnitAbilityQuery(fixture), List.of(ability));
    }

    @Test
    void getWeapons(SeededRng random) {
        var weapon = random.<Weapon>get();
        when(type.getWeapons()).thenReturn(List.of(weapon));
        assertThat(fixture.getWeapons()).containsExactly(weapon);
    }

    @Test
    void setActiveAbilities() {
        testSetter(
                Unit::getActiveAbilities,
                Unit::setActiveAbilities,
                List.of(),
                List.of(ability),
                new GenericEvent(fixture)
        );
    }

    @Test
    void getDistance() {
        var distance = random.nextDouble();
        fixture.setLocation(tile);
        when(tile.getDistance(unit)).thenReturn(distance);
        assertThat(fixture.getDistance(unit)).isEqualTo(distance);
    }

    @Test
    void getDistance_WhenThisUnitIsInAnotherUnit_ThenNaN() {
        fixture.setLocation(unit);
        assertThat(fixture.getDistance(tile)).isNaN();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isAlly(boolean value) {
        var player = random.pick(player1, player2, player3, player4);
        fixture.setOwner(player);
        when(player.isAlly(unit)).thenReturn(value);
        initializeFixture();
        assertThat(fixture.isAlly(unit)).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isEnemy(boolean value) {
        var player = random.pick(player1, player2, player3, player4);
        fixture.setOwner(player);
        when(player.isEnemy(unit)).thenReturn(value);
        initializeFixture();
        assertThat(fixture.isEnemy(unit)).isEqualTo(value);
    }

    @Test
    void getAssociatedObjects() {
        var player = random.pick(player1, player2, player3, player4);
        fixture.setOwner(player);
        fixture.setActiveAbilities(List.of(ability));
        fixture.setLocation(unit);
        initializeFixture();
        assertThat(fixture.getAssociatedObjects()).containsExactly(type, player, ability);
    }

    @Test
    void getAssociatedObjects_WhenTheUnitIsOnATile_ThenIncludesTheTile() {
        var player = random.pick(player1, player2, player3, player4);
        fixture.setOwner(player);
        fixture.setActiveAbilities(List.of(ability));
        fixture.setLocation(tile);
        initializeFixture();
        assertThat(fixture.getAssociatedObjects()).containsExactly(type, player, ability, tile);
    }

    @Test
    void testToString() {
        var name = random.nextString();
        when(type.getName()).thenReturn(name);
        when(tile.toString()).thenReturn(random.nextString());
        fixture.setLocation(tile);
        assertThat(fixture).hasToString("%s(%s)", name, tile);
    }

    @Override
    UnitImpl create() {
        return new UnitImpl().setType(type);
    }

}
