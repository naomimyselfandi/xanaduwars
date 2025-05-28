package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TileDestroyedEvent;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class KeystoneRuleTest {

    @Mock
    private Player player;

    @Mock
    private Tile tile;

    @InjectMocks
    private TileDestroyedEvent event;

    @Mock
    private Filter<Tile> filter;

    @InjectMocks
    private KeystoneRule fixture;

    @Test
    void handle() {
        when(tile.owner()).thenReturn(Optional.of(player));
        assertThat(fixture.handle(event, None.NONE)).isEqualTo(None.NONE);
        verify(player).defeat();
    }

}
