package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ability;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BuildAbilityTest {

    @Mock
    private Player player;

    @Mock
    private TileType foundationType, tileType, otherType;

    @Mock
    private Ruleset ruleset;

    @Mock
    private GameState gameState;

    @Mock
    private Tile tile;

    @Mock
    private Unit builder;

    private BuildAbility fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(tileType.buildTime()).thenReturn(2);
        when(gameState.ruleset()).thenReturn(ruleset);
        when(tileType.foundation()).thenReturn(Optional.of(foundationType));
        when(builder.tile()).thenReturn(Optional.of(tile));
        when(builder.owner()).thenReturn(Optional.of(player));
        var name = random.nextName();
        fixture = new BuildAbility(name, Set.of(tileType));
    }

    @Test
    void enumerateTargets() {
        assertThat(fixture.enumerateTargets(gameState)).containsExactlyElementsOf(fixture.tileTypes());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected) {
        when(tileType.foundation()).thenReturn(Optional.of(foundationType));
        when(tile.type()).thenReturn(expected ? foundationType : otherType);
        when(builder.tile()).thenReturn(Optional.of(tile));
        assertThat(fixture.test(builder, tileType)).isEqualTo(expected);
    }

    @Test
    void test_WhenTheTileTypeIsNotSpecified_ThenFalse() {
        lenient().when(otherType.foundation()).thenReturn(Optional.of(foundationType));
        lenient().when(tile.type()).thenReturn(foundationType);
        when(builder.tile()).thenReturn(Optional.of(tile));
        assertThat(fixture.test(builder, otherType)).isFalse();
    }

    @Test
    void execute_Commence() {
        when(builder.hp()).thenReturn(new Percent(0.3));
        assertThat(fixture.execute(builder, tileType)).isEqualTo(Execution.SUCCESSFUL);
        verify(tile).construction(new Construction(tileType, new Percent(0.15)));
    }

    @Test
    void execute_Continue() {
        when(tile.construction()).thenReturn(Optional.of(new Construction(tileType, new Percent(0.1))));
        when(builder.hp()).thenReturn(new Percent(0.3));
        assertThat(fixture.execute(builder, tileType)).isEqualTo(Execution.SUCCESSFUL);
        verify(tile).construction(new Construction(tileType, new Percent(0.25)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.5, 0.6})
    void execute_Complete(double existingProgress) {
        when(tile.construction()).thenReturn(Optional.of(new Construction(tileType, new Percent(existingProgress))));
        when(builder.hp()).thenReturn(new Percent(1.0));
        assertThat(fixture.execute(builder, tileType)).isEqualTo(Execution.SUCCESSFUL);
        verify(tile).createStructure(tileType, player);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            SUPPLIES,false,50,50
            SUPPLIES,true,50,0
            AETHER,false,50,50
            AETHER,true,50,0
            """)
    void cost(Resource resource, boolean inProgress, int cost, int expected) {
        when(foundationType.costs()).thenReturn(Map.of(resource, cost));
        if (inProgress) {
            when(tile.construction()).thenReturn(Optional.of(new Construction(foundationType, new Percent(0.5))));
        }
        assertThat(fixture.cost(resource, builder, foundationType)).isEqualTo(expected);
    }

}
