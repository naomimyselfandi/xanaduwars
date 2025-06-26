package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CommandProcessorImplTest {

    @Mock
    private Player player;

    @Mock
    private GameState gameState, copyState;

    private CommandDto commandDto;

    @Mock
    private CommandProcessorHelper commandProcessorHelper;

    @InjectMocks
    private CommandProcessorImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        commandDto = random.get();
    }

    @Test
    void process_WhenTheGameStateIsLimitedAndProcessingSucceeds_ThenReportsSuccess() throws ConflictException {
        when(gameState.isLimitedCopy()).thenReturn(true);
        when(commandProcessorHelper.process(gameState, commandDto)).thenReturn(Result.okay());
        assertThat(fixture.process(gameState, commandDto)).isEqualTo(Result.okay());
    }

    @Test
    void process_WhenTheGameStateIsLimitedAndProcessingFails_ThenThrows(SeededRng random) throws ConflictException {
        var fail = random.<Result.Fail>get();
        when(gameState.isLimitedCopy()).thenReturn(true);
        when(commandProcessorHelper.process(gameState, commandDto)).thenReturn(fail);
        assertThatThrownBy(() -> fixture.process(gameState, commandDto))
                .isInstanceOf(ConflictException.class)
                .hasMessage(fail.message());
    }

    @Test
    void process_WhenTheGameStateIsNotLimitedAndProcessingSucceeds_ThenReportsSuccess() throws ConflictException {
        when(copyState.isLimitedCopy()).thenReturn(true);
        when(gameState.isLimitedCopy()).thenReturn(false);
        when(gameState.limitedTo(player)).thenReturn(copyState);
        when(gameState.getActivePlayer()).thenReturn(player);
        when(commandProcessorHelper.process(gameState, commandDto)).thenReturn(Result.okay());
        when(commandProcessorHelper.process(copyState, commandDto)).thenReturn(Result.okay());
        assertThat(fixture.process(gameState, commandDto)).isEqualTo(Result.okay());
    }

    @Test
    void process_WhenTheGameStateIsNotLimitedAndProcessingFailsDueToAvailableInformation_ThenThrows(
            SeededRng random
    ) throws ConflictException {
        var fail = random.<Result.Fail>get();
        when(copyState.isLimitedCopy()).thenReturn(true);
        when(gameState.isLimitedCopy()).thenReturn(false);
        when(gameState.limitedTo(player)).thenReturn(copyState);
        when(gameState.getActivePlayer()).thenReturn(player);
        when(commandProcessorHelper.process(gameState, commandDto)).thenReturn(fail);
        when(commandProcessorHelper.process(copyState, commandDto)).thenReturn(Result.okay());
        assertThat(fixture.process(gameState, commandDto)).isEqualTo(fail);
    }

    @Test
    void process_WhenTheGameStateIsNotLimitedAndProcessingFailsDueToHiddenInformation_ThenReportsFailure(
            SeededRng random
    ) throws ConflictException {
        var fail = random.<Result.Fail>get();
        when(copyState.isLimitedCopy()).thenReturn(true);
        when(gameState.isLimitedCopy()).thenReturn(false);
        when(gameState.limitedTo(player)).thenReturn(copyState);
        when(gameState.getActivePlayer()).thenReturn(player);
        when(commandProcessorHelper.process(copyState, commandDto)).thenReturn(fail);
        assertThatThrownBy(() -> fixture.process(gameState, commandDto))
                .isInstanceOf(ConflictException.class)
                .hasMessage(fail.message());
        verify(commandProcessorHelper, never()).process(gameState, commandDto);
    }

    @Test
    void replay() throws ConflictException {
        fixture.replay(gameState, commandDto);
        verify(commandProcessorHelper).process(gameState, commandDto);
        verify(gameState, never()).limitedTo(any());
    }

    @Test
    void replay_WhenTheReplayFails_ThenThrows() throws ConflictException {
        var e = new ConflictException();
        when(commandProcessorHelper.process(gameState, commandDto)).thenThrow(e);
        assertThatThrownBy(() -> fixture.replay(gameState, commandDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Replaying %s failed!", commandDto)
                .hasCause(e);
    }

}
