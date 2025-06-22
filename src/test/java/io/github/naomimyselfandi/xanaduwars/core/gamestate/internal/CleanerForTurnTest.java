package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Turn;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CleanerForTurnTest {

    private Turn turn;

    @Mock
    private Player alice, bob, charlie, dave;

    private List<Player> players;

    @Mock
    private GameState gameState;

    @InjectMocks
    private CleanerForTurn fixture;

    @BeforeEach
    void setup(SeededRng random) {
        turn = new Turn(random.nextInt(0, 100) * 4);
        when(gameState.getTurn()).thenReturn(turn);
        when(gameState.getActivePlayer()).thenReturn(alice);
        players = List.of(alice, bob, charlie, dave);
        when(gameState.getPlayers()).thenReturn(players);
    }

    @Test
    void clean_WhenThereIsNothingToDo_ThenDoesNothing() {
        fixture.clean(gameState);
        verify(gameState, never()).setTurn(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void clean_WhenThePassedFlagIsSet_ThenAdvancesTheTurn(int defeated) {
        players.stream().limit(defeated).forEach(player -> when(player.isDefeated()).thenReturn(true));
        when(gameState.isPassed()).thenReturn(true);
        fixture.clean(gameState);
        verify(gameState).setPassed(false);
        verify(gameState).setTurn(turn.plus(Math.max(defeated, 1)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void clean_WhenTheActivePlayerIsDefeated_ThenAdvancesTheTurn(int defeated) {
        players.stream().limit(defeated).forEach(player -> when(player.isDefeated()).thenReturn(true));
        fixture.clean(gameState);
        verify(gameState).setTurn(turn.plus(defeated));
    }

    @Test
    void clean_WhenEveryPlayerIsDefeated_ThenDoesNothing() {
        players.forEach(player -> when(player.isDefeated()).thenReturn(true));
        fixture.clean(gameState);
        verify(gameState, never()).setTurn(any());
    }

}
