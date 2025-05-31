package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionWithTileTargetTest {

    @Mock
    private GameState gameState;

    @Mock
    private Tile tile0, tile1, tile2, tile3;

    @BeforeEach
    void setup() {
        when(gameState.tiles()).thenReturn(List.of(List.of(tile0, tile1), List.of(tile2, tile3)));
    }

    private final ActionWithTileTarget<Element> fixture = new ActionWithTileTarget<>() {

        @Override
        public Name name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Execution execute(Element user, Tile target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void enumerateTargets() {
        assertThat(fixture.enumerateTargets(gameState)).containsExactly(tile0, tile1, tile2, tile3);
    }

}
