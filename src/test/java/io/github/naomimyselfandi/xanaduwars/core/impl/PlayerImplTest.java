package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.xanaduwars.core.messages.DefeatedEvent;
import io.github.naomimyselfandi.xanaduwars.core.messages.ReadyStateQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerImplTest extends GameStateAwareTest<PlayerImpl> {

    @Mock
    private Player player;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit, anotherUnit, yetAnotherUnit;

    @Mock
    private Ability spell;

    @Mock
    private Commander commander, anotherCommander;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void getPosition(int position) {
        assertThat(fixture.setPosition(position)).isSameAs(fixture);
        assertThat(fixture.getPosition()).isEqualTo(position);
    }

    @Test
    void setTeam() {
        testSetter(Player::getTeam, Player::setTeam, 0, random.nextInt());
    }

    @Test
    void setCommander() {
        testSetter(Player::getCommander, Player::setCommander, commander, anotherCommander);
    }

    @Test
    void asPlayer() {
        assertThat(fixture.asPlayer()).isSameAs(fixture);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isReady(boolean value) {
        when(gameState.evaluate(new ReadyStateQuery(fixture))).thenReturn(value);
        initializeFixture();
        assertThat(fixture.isReady()).isEqualTo(value);
    }

    @Test
    void setAbilities() {
        testSetter(Player::getAbilities, Player::setAbilities, List.of(), List.of(spell));
    }

    @Test
    void setActiveAbilities() {
        testSetter(Player::getActiveAbilities, Player::setActiveAbilities, List.of(), List.of(spell));
    }

    @Test
    void setUsedAbilities() {
        testSetter(Player::getUsedAbilities, Player::setUsedAbilities, List.of(), List.of(spell));
    }

    @Test
    void setDefeated() {
        initializeFixture();
        assertThat(fixture.isDefeated()).isFalse();
        assertThat(fixture.setDefeated(true)).isSameAs(fixture);
        verify(gameState).dispatch(new DefeatedEvent(fixture));
        clearInvocations(gameState);
        assertThat(fixture.isDefeated()).isTrue();
        assertThat(fixture.setDefeated(true)).isSameAs(fixture);
        verifyNoInteractions(gameState);
        assertThat(fixture.setDefeated(false)).isSameAs(fixture);
        verify(gameState).clearQueryCache();
        clearInvocations(gameState);
        assertThat(fixture.isDefeated()).isFalse();
        assertThat(fixture.setDefeated(false)).isSameAs(fixture);
        verifyNoInteractions(gameState);
    }

    @Test
    void setSupplies() {
        testSetter(Player::getSupplies, PlayerImpl::setSupplies, 0, random.not(0));
    }

    @Test
    void setAether() {
        testSetter(Player::getAether, PlayerImpl::setAether, 0, random.not(0));
    }

    @Test
    void setFocus() {
        testSetter(Player::getFocus, PlayerImpl::setFocus, 0, random.not(0));
    }

    @Test
    void getUnits() {
        when(gameState.getUnits()).then(_ -> Stream.of(unit, anotherUnit, yetAnotherUnit));
        when(unit.getOwner()).thenReturn(fixture);
        when(anotherUnit.getOwner()).thenReturn(player);
        when(yetAnotherUnit.getOwner()).thenReturn(fixture);
        initializeFixture();
        assertThat(fixture.getUnits()).containsExactly(unit, yetAnotherUnit);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void perceives_Tile(boolean perceivedByUnit1, boolean perceivedByUnit2, boolean expected) {
        when(gameState.getUnits()).then(_ -> Stream.of(unit, anotherUnit));
        lenient().when(unit.getOwner()).thenReturn(fixture);
        lenient().when(anotherUnit.getOwner()).thenReturn(fixture);
        lenient().when(unit.perceives(tile)).thenReturn(perceivedByUnit1);
        lenient().when(anotherUnit.perceives(tile)).thenReturn(perceivedByUnit2);
        initializeFixture();
        assertThat(fixture.perceives(tile)).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void perceives_Unit(boolean ally, boolean perceivedByUnit1, boolean perceivedByUnit2, boolean expected) {
        lenient().when(gameState.getUnits()).then(_ -> Stream.of(unit, anotherUnit, yetAnotherUnit));
        lenient().when(yetAnotherUnit.getOwner()).thenReturn(player);
        var team1 = random.nextInt();
        var team2 = ally ? team1 : random.not(team1);
        fixture.setTeam(team1);
        when(player.getTeam()).thenReturn(team2);
        lenient().when(unit.getOwner()).thenReturn(fixture);
        lenient().when(anotherUnit.getOwner()).thenReturn(fixture);
        lenient().when(unit.perceives(yetAnotherUnit)).thenReturn(perceivedByUnit1);
        lenient().when(anotherUnit.perceives(yetAnotherUnit)).thenReturn(perceivedByUnit2);
        initializeFixture();
        assertThat(fixture.perceives(yetAnotherUnit)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isAlly_Player(boolean value) {
        var team1 = random.nextInt();
        var team2 = value ? team1 : random.not(team1);
        fixture.setTeam(team1);
        when(player.getTeam()).thenReturn(team2);
        assertThat(fixture.isAlly(player)).isEqualTo(value);
    }

    @Test
    void isAlly_Tile() {
        assertThat(fixture.isAlly(tile)).isFalse();
        verifyNoInteractions(tile);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isAlly_Unit(boolean value) {
        var team1 = random.nextInt();
        var team2 = value ? team1 : random.not(team1);
        fixture.setTeam(team1);
        when(player.getTeam()).thenReturn(team2);
        when(unit.getOwner()).thenReturn(player);
        assertThat(fixture.isAlly(unit)).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isEnemy_Player(boolean value) {
        var team1 = random.nextInt();
        var team2 = value ? random.not(team1) : team1;
        fixture.setTeam(team1);
        when(player.getTeam()).thenReturn(team2);
        assertThat(fixture.isEnemy(player)).isEqualTo(value);
    }

    @Test
    void isEnemy_Tile() {
        assertThat(fixture.isEnemy(tile)).isFalse();
        verifyNoInteractions(tile);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isEnemy_Unit(boolean value) {
        var team1 = random.nextInt();
        var team2 = value ? random.not(team1) : team1;
        fixture.setTeam(team1);
        when(player.getTeam()).thenReturn(team2);
        when(unit.getOwner()).thenReturn(player);
        assertThat(fixture.isEnemy(unit)).isEqualTo(value);
    }

    @Test
    void getAssociatedObjects() {
        fixture.setActiveAbilities(List.of(spell));
        assertThat(fixture.getAssociatedObjects()).containsExactly(commander, spell);
    }

    @Test
    void testToString() {
        var name = random.nextString();
        var position = random.nextInt(0, 4);
        fixture.setPosition(position);
        when(commander.getName()).thenReturn(name);
        initializeFixture();
        assertThat(fixture).hasToString("%s(%s)", name, position);
    }

    @Override
    PlayerImpl create() {
        return new PlayerImpl().setCommander(commander);
    }

}
