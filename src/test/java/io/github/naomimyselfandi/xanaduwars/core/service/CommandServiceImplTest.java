package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandSequence;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.core.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandServiceImplTest {

    @Mock
    private CommandSequence commandSequence;

    @Mock
    private Player player;

    @Mock
    private GameState gameState, copy;

    @Mock
    private CopyMachine copyMachine;

    @InjectMocks
    private CommandServiceImpl fixture;

    @Test
    void submit() throws CommandException {
        when(gameState.isRedacted()).thenReturn(false);
        when(gameState.getActivePlayer()).thenReturn(player);
        when(copyMachine.createRedactedCopy(gameState, player)).thenReturn(copy);
        when(commandSequence.submit(copy)).thenReturn(true);
        fixture.submit(gameState, commandSequence);
        var inOrder = inOrder(commandSequence);
        inOrder.verify(commandSequence).submit(copy);
        inOrder.verify(commandSequence).submit(gameState);
    }

    @Test
    void submit_WhenTheSequenceFails_ThenStops() throws CommandException {
        var exception = new CommandException();
        when(gameState.isRedacted()).thenReturn(false);
        when(gameState.getActivePlayer()).thenReturn(player);
        when(copyMachine.createRedactedCopy(gameState, player)).thenReturn(copy);
        when(commandSequence.submit(copy)).thenThrow(exception);
        assertThatThrownBy(() -> fixture.submit(gameState, commandSequence)).isEqualTo(exception);
        verify(commandSequence, never()).submit(gameState);
    }

    @Test
    void submit_WhenTheSequenceIsInterrupted_ThenStops() throws CommandException {
        when(gameState.isRedacted()).thenReturn(false);
        when(gameState.getActivePlayer()).thenReturn(player);
        when(copyMachine.createRedactedCopy(gameState, player)).thenReturn(copy);
        when(commandSequence.submit(copy)).thenReturn(false);
        assertThatThrownBy(() -> fixture.submit(gameState, commandSequence))
                .isInstanceOf(CommandException.class)
                .hasMessage("Invalid command.");
        verify(commandSequence, never()).submit(gameState);
    }

    @Test
    void submit_WhenTheGameStateIsRedacted_ThenSubmitsTheSequenceDirectly() throws CommandException {
        when(gameState.isRedacted()).thenReturn(true);
        fixture.submit(gameState, commandSequence);
        verify(commandSequence).submit(gameState);
        verifyNoInteractions(copyMachine);
    }

}
