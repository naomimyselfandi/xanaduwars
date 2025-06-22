package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CleanerForMemoryTest {

    @Mock
    private Player player0, player1;

    @Mock
    private Tile tile0, tile1;

    @Mock
    private StructureType structureType;

    @Mock
    private Structure structure;

    @Mock
    private GameState gameState;

    @InjectMocks
    private CleanerForMemory fixture;

    @Test
    void clean(SeededRng random) {
        when(structure.getType()).thenReturn(structureType);
        when(gameState.getPlayers()).thenReturn(List.of(player0, player1));
        when(gameState.getTiles()).thenReturn(new TreeMap<>(Map.of(random.get(), tile0, random.get(), tile1)));
        when(tile0.getStructure()).thenReturn(structure);
        when(player0.canSee(tile0)).thenReturn(true);
        when(player0.canSee(tile1)).thenReturn(true);
        when(player1.canSee(tile0)).thenReturn(false);
        when(player1.canSee(tile1)).thenReturn(false);
        fixture.clean(gameState);
        verify(tile0).setMemory(player0, structureType);
        verify(tile0, never()).setMemory(player1, structureType);
        verify(tile1).setMemory(player0, null);
        verify(tile1, never()).setMemory(player1, null);
    }

}
