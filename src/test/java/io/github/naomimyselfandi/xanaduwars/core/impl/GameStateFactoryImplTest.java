package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateFactoryImplTest {

    @Mock
    private Version version;

    @Mock
    private Commander commander;

    @Mock
    private Ability spell;

    @Mock
    private TileType tileType;

    private GameStateFactoryImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(version.getDeclarations()).then(_ -> random.shuffle(commander, spell, tileType).stream());
        when(commander.getSignatureSpells()).thenReturn(List.of(spell));
        fixture = new GameStateFactoryImpl();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            3,4,2
            4,5,4
            """)
    void create(int width, int height, int playerCount) {
        var gameState = fixture.create(width, height, playerCount, version);
        assertThat(gameState.getPlayers()).hasSize(playerCount).allSatisfy(player -> {
            assertThat(player).isExactlyInstanceOf(PlayerImpl.class);
            assertThat(player.getTeam()).isEqualTo(player.getPosition());
            assertThat(player.getCommander()).isEqualTo(commander);
            assertThat(player.getAbilities()).containsExactly(spell);
        });
        assertThat(gameState.getTiles()).hasSize(width * height).allSatisfy(tile -> {
            assertThat(tile).isExactlyInstanceOf(TileImpl.class);
            assertThat(tile.getType()).isEqualTo(tileType);
        });
        assertThat(gameState.getTurn()).isZero();
        assertThat(gameState.isRedacted()).isFalse();
    }

}
