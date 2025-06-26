package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.CleanupEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.TurnStartEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStarterImplTest {

    @Mock
    private Structure structure;

    @Mock
    private StructureType structureType;

    @Mock
    private Tile tile;

    @Mock
    private Player player0, player1, player2;

    private List<Player> players;

    @Mock
    private Commander commander0, commander1, commander2;

    private Game game;

    @Mock
    private GameState gameState;

    @Mock
    private GameStateFactory gameStateFactory;

    @InjectMocks
    private GameStarterImpl fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        game = random.<Game>get().setStatus(Game.Status.PENDING);
        when(gameStateFactory.create(game.getGameStateData())).thenReturn(gameState);
        players = List.of(player0, player1, player2);
        when(gameState.getPlayers()).thenReturn(players);
        when(gameState.getActivePlayer()).thenReturn(player0);
        when(player0.getId()).thenReturn(new PlayerId(0));
        when(player1.getId()).thenReturn(new PlayerId(1));
        when(player2.getId()).thenReturn(new PlayerId(2));
        this.random = random;
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,1,2,true,true,true,
            0,1,,true,true,false,
            0,,1,true,false,true,
            ,0,1,false,true,true,
            0,,0,true,false,true,Cannot start a game without at least two teams present.
            0,1,2,true,true,false,Cannot start a game before all players have chosen a commander.
            """)
    void start(
            @Nullable Integer team0,
            @Nullable Integer team1,
            @Nullable Integer team2,
            boolean player0HasCommander,
            boolean player1HasCommander,
            boolean player2HasCommander,
            @Nullable String problem
    ) {
        var slots = new TreeMap<PlayerId, PlayerSlot>();
        if (team0 != null) {
            slots.put(new PlayerId(0), random.get());
            when(player0.getTeam()).thenReturn(new Team(team0));
        }
        if (team1 != null) {
            slots.put(new PlayerId(1), random.get());
            when(player1.getTeam()).thenReturn(new Team(team1));
        }
        if (team2 != null) {
            slots.put(new PlayerId(2), random.get());
            when(player2.getTeam()).thenReturn(new Team(team2));
        }
        game.setPlayerSlots(slots);
        when(player0.getCommander()).thenReturn(Optional.ofNullable(player0HasCommander ? commander0 : null));
        when(player1.getCommander()).thenReturn(Optional.ofNullable(player1HasCommander ? commander1 : null));
        when(player2.getCommander()).thenReturn(Optional.ofNullable(player2HasCommander ? commander2 : null));
        if (problem == null) {
            assertThatCode(() -> fixture.start(game)).doesNotThrowAnyException();
            assertThat(game.getStatus()).isEqualTo(Game.Status.ONGOING);
            if (team0 == null) {
                verify(gameState).evaluate(new CleanupEvent());
            } else {
                verify(gameState).evaluate(new TurnStartEvent(player0));
            }
        } else {
            assertThatThrownBy(() -> fixture.start(game))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(problem);
        }
    }

    @Test
    void start_InitializesMemory(SeededRng random) throws ConflictException {
        when(gameState.getStructures()).thenReturn(new TreeMap<>(Map.of(random.get(), structure)));
        when(structure.getType()).thenReturn(structureType);
        when(structure.getTile()).thenReturn(Optional.of(tile));
        var slots = new TreeMap<PlayerId, PlayerSlot>();
        slots.put(new PlayerId(0), random.get());
        when(player0.getTeam()).thenReturn(new Team(0));
        slots.put(new PlayerId(1), random.get());
        when(player1.getTeam()).thenReturn(new Team(1));
        slots.put(new PlayerId(2), random.get());
        when(player2.getTeam()).thenReturn(new Team(2));
        game.setPlayerSlots(slots);
        when(player0.getCommander()).thenReturn(Optional.of(commander0));
        when(player1.getCommander()).thenReturn(Optional.of(commander1));
        when(player2.getCommander()).thenReturn(Optional.of(commander2));
        fixture.start(game);
        verify(tile).setMemory(player0, structureType);
        verify(tile).setMemory(player1, structureType);
        verify(tile).setMemory(player2, structureType);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void start_WhenAPlayerIsAbsent_ThenDefeatsThem(int index) {
        var slots = new TreeMap<PlayerId, PlayerSlot>();
        if (index != 0) {
            slots.put(new PlayerId(0), random.get());
            when(player0.getCommander()).thenReturn(Optional.of(commander0));
            when(player0.getTeam()).thenReturn(new Team(0));
        }
        if (index != 1) {
            slots.put(new PlayerId(1), random.get());
            when(player1.getCommander()).thenReturn(Optional.of(commander1));
            when(player1.getTeam()).thenReturn(new Team(1));
        }
        if (index != 2) {
            slots.put(new PlayerId(2), random.get());
            when(player2.getCommander()).thenReturn(Optional.of(commander2));
            when(player2.getTeam()).thenReturn(new Team(2));
        }
        game.setPlayerSlots(slots);
        assertThatCode(() -> fixture.start(game)).doesNotThrowAnyException();
        assertThat(game.getStatus()).isEqualTo(Game.Status.ONGOING);
        for (var i = 0; i < 3; i++) {
            verify(players.get(i), i == index ? times(1) : never()).defeat();
        }
        if (index == 0) {
            verify(gameState).evaluate(new CleanupEvent());
        } else {
            verify(gameState).evaluate(new TurnStartEvent(player0));
        }
    }

}
