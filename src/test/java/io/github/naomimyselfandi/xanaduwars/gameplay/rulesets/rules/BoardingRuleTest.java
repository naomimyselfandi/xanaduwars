package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.EntryQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BoardingRuleTest {

    @Mock
    private Player player, anotherPlayer;

    @Mock
    private Unit unit, transport, cargo;

    @Mock
    private Tile tile;

    private EntryQuery query;

    @InjectMocks
    private BoardingRule fixture;

    @BeforeEach
    void setup() {
        query = new EntryQuery(unit, tile);
    }

    @Test
    void handle(SeededRng random) {
        assertThat(fixture.handle(query, random.nextDouble())).isNaN();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true,true,false
            false,true,true,true
            true,false,true,true
            true,true,false,true
            """)
    void handles(boolean sameOwner, boolean empty, boolean tagged, boolean expected, SeededRng random) {
        var foo = random.nextTag("F");
        var bar = random.nextTag("B");
        when(tile.unit()).thenReturn(Optional.of(transport));
        when(unit.owner()).thenReturn(Optional.of(player));
        when(transport.owner()).thenReturn(Optional.of(sameOwner ? player : anotherPlayer));
        when(transport.cargo()).thenReturn(empty ? List.of() : List.of(cargo));
        when(unit.tags()).thenReturn(TagSet.of(foo));
        when(transport.hangar()).thenReturn(tagged ? TagSet.of(foo, bar) : TagSet.of(bar));
        assertThat(fixture.handles(query, random.nextDouble())).isEqualTo(expected);
    }

    @Test
    void handles_WhenTheTileHasNoUnit_ThenFalse(SeededRng random) {
        when(tile.unit()).thenReturn(Optional.empty());
        assertThat(fixture.handles(query, random.nextDouble())).isFalse();
    }

}
