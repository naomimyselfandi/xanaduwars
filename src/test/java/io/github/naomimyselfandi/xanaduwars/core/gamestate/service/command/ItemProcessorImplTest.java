package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemProcessorImplTest {

    @Mock
    private GameState gameState;

    @Mock
    private Player player;

    @Mock
    private Unit actor;

    @Mock
    private NormalAction action;

    private List<Object> targets;

    @InjectMocks
    private ItemProcessorImpl fixture;

    @BeforeEach
    void setup() {
        when(actor.getGameState()).thenReturn(gameState);
        when(actor.getOwner()).thenReturn(Optional.of(player));
        targets = List.of(new Object(), new Object());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,,0,0,0,0,0,0,,,true
            ,,10,20,30,40,50,60,,,true
            foo,bar,10,20,30,40,50,60,baz,foo,false
            ,foo,10,20,30,40,50,60,bar,foo,false
            ,,10,20,30,40,50,60,bar,bar,true
            ,,10,9,30,40,50,60,bar,Insufficient supplies.,false
            ,,10,20,30,29,50,60,bar,Insufficient aether.,false
            ,,10,20,30,40,50,49,bar,Insufficient focus.,false
            """)
    void process(
            @Nullable String usageQueryResult,
            @Nullable String targetQueryResult,
            int supplyCost,
            int suppliesAvailable,
            int aetherCost,
            int aetherAvailable,
            int focusCost,
            int focusAvailable,
            @Nullable String executionResult,
            @Nullable String problem,
            boolean shouldExecute
    ) {
        when(gameState.evaluate(new ActionUsageQuery(actor, action))).thenReturn(result(usageQueryResult));
        when(gameState.evaluate(new ActionTargetQuery(actor, action, targets))).thenReturn(result(targetQueryResult));
        when(gameState.evaluate(new SupplyCostQuery(actor, action, targets))).thenReturn(supplyCost);
        when(player.getSupplies()).thenReturn(suppliesAvailable);
        when(gameState.evaluate(new AetherCostQuery(actor, action, targets))).thenReturn(aetherCost);
        when(player.getAether()).thenReturn(aetherAvailable);
        when(gameState.evaluate(new FocusCostQuery(actor, action, targets))).thenReturn(focusCost);
        when(player.getFocus()).thenReturn(focusAvailable);
        when(gameState.evaluate(new ActionExecutionEvent(actor, action, targets))).thenReturn(result(executionResult));
        assertThat(fixture.process(actor, new Command.Item(action, targets))).isEqualTo(result(problem));
        if (shouldExecute) {
            var inOrder = inOrder(player, gameState);
            inOrder.verify(player).setSupplies(suppliesAvailable - supplyCost);
            inOrder.verify(player).setAether(aetherAvailable - aetherCost);
            inOrder.verify(player).setFocus(focusAvailable - focusCost);
            inOrder.verify(gameState).evaluate(new ActionExecutionEvent(actor, action, targets));
        } else {
            verify(player, never()).setSupplies(anyInt());
            verify(player, never()).setAether(anyInt());
            verify(player, never()).setFocus(anyInt());
            verify(gameState, never()).evaluate(any(ActionExecutionEvent.class));
        }
    }
    
    private static Result result(@Nullable String message) {
        return message == null ? Result.okay() : Result.fail(message);
    }

}
