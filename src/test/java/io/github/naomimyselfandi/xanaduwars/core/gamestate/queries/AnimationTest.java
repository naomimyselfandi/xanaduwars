package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Direction;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AnimationTest {

    @Mock
    private GameState gameState;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit;

    @ParameterizedTest
    @CsvSource(textBlock = """
            NORTH,NORTH
            EAST,EAST
            SOUTH,SOUTH
            WEST,WEST
            """)
    void of(Direction direction, Animation animation) {
        assertThat(Animation.of(direction)).isEqualTo(animation);
    }

    @EnumSource
    @ParameterizedTest
    void play(Animation animation) {
        when(unit.getTile()).thenReturn(Optional.of(tile));
        when(unit.getGameState()).thenReturn(gameState);
        animation.play(unit);
        verify(gameState).evaluate(new AnimationEvent(tile, animation));
    }

}
