package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ActionStatusQueryTest {

    @Mock
    private Player activePlayer, inactivePlayer;

    @Mock
    private GameState gameState;

    @Mock
    private Unit subject;

    private ActionStatusQuery fixture;

    @BeforeEach
    void setup() {
        when(subject.getGameState()).thenReturn(gameState);
        when(gameState.getActivePlayer()).thenReturn(activePlayer);
        fixture = new ActionStatusQuery(subject);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultValue(boolean ownerIsActive) {
        when(subject.getOwner()).thenReturn(Optional.of(ownerIsActive ? activePlayer : inactivePlayer));
        assertThat(fixture.defaultValue() instanceof Result.Okay).isEqualTo(ownerIsActive);
    }

}
