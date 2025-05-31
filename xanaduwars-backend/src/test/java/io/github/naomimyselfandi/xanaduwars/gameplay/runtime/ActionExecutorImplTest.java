package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionCleanupEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionGroupValidation;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionValidation;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.StandardTargetValidation;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActionExecutorImplTest {

    @Mock
    private Player player;

    @Mock
    private Action<Unit, Tile> foo, bar;

    @Mock
    private Tile fooTarget, barTarget;

    private List<ActionItem<Unit, Tile>> actionItems;

    @Mock
    private GameState gameState;

    @Mock
    private Unit unit;

    private ActionExecutorImpl fixture;

    @BeforeEach
    void setup() {
        actionItems = List.of(new ActionItem<>(foo, fooTarget), new ActionItem<>(bar, barTarget));
        when(unit.gameState()).thenReturn(gameState);
        when(gameState.activePlayer()).thenReturn(player);
        fixture = new ActionExecutorImpl();
    }

    @Test
    void execute() {
        var resources = Map.of(Resource.SUPPLIES, 200, Resource.AETHER, 150, Resource.FOCUS, 100);
        when(player.resources()).thenReturn(resources);
        valid(new ActionGroupValidation(List.of(foo, bar)));
        valid(new ActionValidation(unit));
        valid(new StandardTargetValidation<>(foo, unit, fooTarget));
        when(foo.cost(Resource.SUPPLIES, unit, fooTarget)).thenReturn(20);
        when(foo.cost(Resource.AETHER, unit, fooTarget)).thenReturn(15);
        when(foo.cost(Resource.FOCUS, unit, fooTarget)).thenReturn(10);
        when(foo.execute(unit, fooTarget)).thenReturn(Execution.SUCCESSFUL);
        valid(new StandardTargetValidation<>(bar, unit, barTarget));
        when(bar.cost(Resource.SUPPLIES, unit, barTarget)).thenReturn(30);
        when(bar.cost(Resource.AETHER, unit, barTarget)).thenReturn(25);
        when(bar.cost(Resource.FOCUS, unit, barTarget)).thenReturn(20);
        when(bar.execute(unit, barTarget)).thenReturn(Execution.SUCCESSFUL);
        assertThatCode(() -> fixture.execute(actionItems, unit)).doesNotThrowAnyException();
        var inOrder = inOrder(player, foo, bar, unit, gameState);
        inOrder.verify(player).resource(Resource.SUPPLIES, 180);
        inOrder.verify(player).resource(Resource.AETHER, 135);
        inOrder.verify(player).resource(Resource.FOCUS, 90);
        inOrder.verify(foo).execute(unit, fooTarget);
        inOrder.verify(player).resource(Resource.SUPPLIES, 170);
        inOrder.verify(player).resource(Resource.AETHER, 125);
        inOrder.verify(player).resource(Resource.FOCUS, 80);
        inOrder.verify(bar).execute(unit, barTarget);
        inOrder.verify(gameState).evaluate(new ActionCleanupEvent(unit));
    }

    @Test
    void execute_WhenAnActionIsInterrupted_ThenSkipsAnyRemainingActions() {
        valid(new ActionGroupValidation(List.of(foo, bar)));
        valid(new ActionValidation(unit));
        valid(new StandardTargetValidation<>(foo, unit, fooTarget));
        when(foo.execute(unit, fooTarget)).thenReturn(Execution.INTERRUPTED);
        assertThatCode(() -> fixture.execute(actionItems, unit)).doesNotThrowAnyException();
        var inOrder = inOrder(foo, unit, gameState);
        inOrder.verify(foo).execute(unit, fooTarget);
        verify(bar, never()).execute(unit, barTarget);
        inOrder.verify(gameState).evaluate(new ActionCleanupEvent(unit));
    }

    @Test
    void execute_WhenACostCannotBePaid_ThenThrows() {
        var resources = Map.of(Resource.SUPPLIES, 200, Resource.AETHER, 150, Resource.FOCUS, 100);
        var exception = new ActionException(foo, Resource.AETHER);
        when(player.resources()).thenReturn(resources);
        valid(new ActionGroupValidation(List.of(foo, bar)));
        valid(new ActionValidation(unit));
        valid(new StandardTargetValidation<>(foo, unit, fooTarget));
        when(foo.cost(Resource.SUPPLIES, unit, fooTarget)).thenReturn(20);
        when(foo.cost(Resource.AETHER, unit, fooTarget)).thenReturn(201);
        assertThatThrownBy(() -> fixture.execute(actionItems, unit)).isEqualTo(exception);
        verify(foo, never()).execute(unit, fooTarget);
        verify(bar, never()).execute(unit, barTarget);
        verify(gameState).evaluate(new ActionCleanupEvent(unit));
    }

    @Test
    void execute_WhenTheActionGroupIsInvalid_ThenThrows() {
        var rule = invalid(new ActionGroupValidation(List.of(foo, bar)));
        assertThatThrownBy(() -> fixture.execute(actionItems, unit)).isEqualTo(new ActionException(null, rule));
        verify(foo, never()).execute(unit, fooTarget);
        verify(bar, never()).execute(unit, barTarget);
        verify(player, never()).resource(any(), anyInt());
        verify(gameState, never()).evaluate(new ActionCleanupEvent(unit));
    }

    @Test
    void execute_WhenTheElementIsNotReady_ThenThrows() {
        valid(new ActionGroupValidation(List.of(foo, bar)));
        var rule = invalid(new ActionValidation(unit));
        assertThatThrownBy(() -> fixture.execute(actionItems, unit)).isEqualTo(new ActionException(null, rule));
        verify(foo, never()).execute(unit, fooTarget);
        verify(bar, never()).execute(unit, barTarget);
        verify(player, never()).resource(any(), anyInt());
        verify(gameState, never()).evaluate(new ActionCleanupEvent(unit));
    }

    private void valid(Validation validation) {
        when(gameState.evaluate(eq(validation))).thenReturn(true);
        when(gameState.evaluate(eq(validation), anyConsumer())).thenReturn(true);
    }

    private Rule<?, ?> invalid(Validation validation) {
        Rule<?, ?> rule = mock(Rule.class);
        when(gameState.evaluate(eq(validation), anyConsumer())).then(invocation -> {
            invocation.<Consumer<Rule<?, ?>>>getArgument(1).accept(rule);
            return false;
        });
        return rule;
    }

    private static Consumer<Rule<?, ?>> anyConsumer() {
        return any();
    }

}
