package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEventForTile;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TileIncomeRuleTest {

    @Mock
    private Player player;

    @Mock
    private Tile tile;

    @InjectMocks
    private TurnStartEventForTile event;

    @Mock
    private Filter<Tile> filter;

    @InjectMocks
    private TileIncomeRule fixture;

    @BeforeEach
    void setup() {
        when(tile.owner()).thenReturn(Optional.of(player));
    }

    @RepeatedTest(10)
    void handle(SeededRng random) {
        var supplies = random.nextInt(1000);
        var aether = random.nextInt(1000);
        var supplyIncome = random.nextInt(1000);
        var aetherIncome = random.nextInt(1000);
        when(player.resources()).thenReturn(Map.of(Resource.SUPPLIES, supplies, Resource.AETHER, aether));
        when(tile.income()).thenReturn(Map.of(Resource.SUPPLIES, supplyIncome, Resource.AETHER, aetherIncome));
        assertThat(fixture.handle(event, None.NONE)).isEqualTo(None.NONE);
        verify(player).resource(Resource.SUPPLIES, supplies + supplyIncome);
        verify(player).resource(Resource.AETHER, aether + aetherIncome);
    }

}
