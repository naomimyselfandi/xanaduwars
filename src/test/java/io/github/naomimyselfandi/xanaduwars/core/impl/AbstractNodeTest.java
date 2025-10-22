package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.xanaduwars.core.messages.AnimationEvent;
import io.github.naomimyselfandi.xanaduwars.core.messages.UnitCreatedEvent;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractNodeTest extends GameStateAwareTest<Node> {

    @Mock
    private Player player;

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit, anotherUnit;

    @Test
    void setUnit() {
        testSetter(Node::getUnit, Node::setUnit, null, unit);
        verify(unit).setLocation(fixture);
        clearInvocations(gameState);
        testSetter(Node::getUnit, Node::setUnit, unit, null);
    }

    @Test
    void setUnit_WhenTheNodeAlreadyHasATile_ThenThrows() {
        fixture.setUnit(unit);
        initializeFixture();
        assertThatThrownBy(() -> fixture.setUnit(anotherUnit))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s already has a unit!", fixture);
        assertThat(fixture.getUnit()).isEqualTo(unit);
    }

    @Test
    void createUnit() {
        initializeFixture();
        assertThat(fixture.createUnit(unitType, player)).isInstanceOfSatisfying(UnitImpl.class, unit -> {
            assertThat(fixture.getUnit()).isSameAs(unit);
            assertThat(unit.getLocation()).isSameAs(fixture);
            assertThat(unit.getType()).isEqualTo(unitType);
            assertThat(unit.getOwner()).isEqualTo(player);
            assertThat(unit.getHpPercent()).isOne();
            assertThat(unit.getGameState()).isEqualTo(gameState);
            verify(gameState).dispatch(new UnitCreatedEvent(unit));
        });
    }

    @Test
    void createUnit_WhenTheNodeAlreadyHasATile_ThenThrows() {
        fixture.setUnit(unit);
        initializeFixture();
        assertThatThrownBy(() -> fixture.createUnit(unitType, player))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s already has a unit!", fixture);
        assertThat(fixture.getUnit()).isEqualTo(unit);
    }

    @Test
    void play() {
        var animation = random.<Animation>get();
        initializeFixture();
        fixture.play(animation);
        verify(gameState).dispatch(new AnimationEvent(fixture, animation));
    }

    @Override
    Node create() {
        return new TileImpl().setType(mock());
    }

}
