package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CommandSequenceForSelfTest {

    @Mock
    private GameState gameState;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void submit(boolean expected, SeededRng random) throws CommandException {
        var sequence = random.<CommandSequenceForSelf>get();
        when(gameState.submitPlayerCommand(sequence.selfCommands())).thenReturn(expected);
        assertThat(sequence.submit(gameState)).isEqualTo(expected);
    }

}
