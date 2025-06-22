package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.ActionGroupQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.ActionStatusQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.CleanupEvent;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CommandProcessorHelperImplTest {

    @Mock
    private GameState gameState;

    private CommandDto commandDto;

    private Command command;

    private Element actor;

    private List<Action> actions;

    @Mock
    private CommandResolver commandResolver;

    @Mock
    private ItemProcessor itemProcessor;

    @InjectMocks
    private CommandProcessorHelperImpl fixture;

    @BeforeEach
    void setup(SeededRng random) throws ConflictException {
        commandDto = random.get();
        command = random.get();
        actor = command.actor();
        actions = command.items().stream().map(Command.Item::action).toList();
        when(commandResolver.resolve(gameState, commandDto)).thenReturn(command);
    }

    @Test
    void process() throws ConflictException {
        when(gameState.evaluate(new ActionStatusQuery(actor))).thenReturn(Result.okay());
        when(gameState.evaluate(new ActionGroupQuery(actor, actions))).thenReturn(Result.okay());
        when(itemProcessor.process(eq(actor), any())).thenReturn(Result.okay());
        assertThat(fixture.process(gameState, commandDto)).isEqualTo(Result.okay());
        verify(gameState).evaluate(new CleanupEvent());
    }

    @Test
    void process_WhenTheStatusIsIncorrect_ThenFails(SeededRng random) throws ConflictException {
        var fail = random.<Result.Fail>get();
        when(gameState.evaluate(new ActionStatusQuery(actor))).thenReturn(fail);
        assertThat(fixture.process(gameState, commandDto)).isEqualTo(fail);
        verifyNoInteractions(itemProcessor);
    }

    @Test
    void process_WhenTheGroupIsIncorrect_ThenFails(SeededRng random) throws ConflictException {
        var fail = random.<Result.Fail>get();
        when(gameState.evaluate(new ActionStatusQuery(actor))).thenReturn(Result.okay());
        when(gameState.evaluate(new ActionGroupQuery(actor, actions))).thenReturn(fail);
        assertThat(fixture.process(gameState, commandDto)).isEqualTo(fail);
        verifyNoInteractions(itemProcessor);
    }

    @Test
    void process_WhenAnItemFails_ThenFails(SeededRng random) throws ConflictException {
        var fail = random.<Result.Fail>get();
        when(gameState.evaluate(new ActionStatusQuery(actor))).thenReturn(Result.okay());
        when(gameState.evaluate(new ActionGroupQuery(actor, actions))).thenReturn(Result.okay());
        when(itemProcessor.process(actor, command.items().get(0))).thenReturn(Result.okay());
        when(itemProcessor.process(actor, command.items().get(1))).thenReturn(fail);
        assertThat(fixture.process(gameState, commandDto)).isEqualTo(fail);
        verify(itemProcessor, never()).process(actor, command.items().get(2));
    }

}
