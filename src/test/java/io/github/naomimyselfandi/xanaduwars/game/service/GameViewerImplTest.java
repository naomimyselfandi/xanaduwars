package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.GameStateDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.game.dto.GameDto;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameViewerImplTest {

    @Mock
    private Player alice, bob, charlie, dave;

    @Mock
    private GameState gameState, limitedCopy;

    private Game game;

    private GameDto gameDto;

    private GameStateDto gameStateDto;

    @Mock
    private GameStateFactory gameStateFactory;

    @Mock
    private Converter<Game, GameDto> gameDtoConverter;

    @Mock
    private Converter<GameState, GameStateDto> gameStateDtoConverter;

    // Can't use @InjectMocks because it gets confused by multiple generics with the same raw types
    private GameViewerImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        game = random.get();
        gameDto = random.get();
        gameStateDto = random.get();
        fixture = new GameViewerImpl(gameStateFactory, gameDtoConverter, gameStateDtoConverter);
    }

    @Test
    void getMetadata() {
        when(gameDtoConverter.convert(game)).thenReturn(gameDto);
        assertThat(fixture.getMetadata(game)).isEqualTo(gameDto);
    }

    @Test
    void getChoices(SeededRng random) {
        var players = game.getGameStateData().getPlayers();
        var playerData = random.pick(players);
        var playerId = new PlayerId(players.indexOf(playerData));
        var expected = new ChoiceDto()
                .setCommander(playerData.getCommanderId())
                .setSpells(playerData.getChosenSpells().getSpellIds());
        assertThat(fixture.getChoices(game, playerId)).isEqualTo(expected);
    }

    @Test
    void view() {
        when(gameStateFactory.create(game.getGameStateData())).thenReturn(gameState);
        when(gameStateDtoConverter.convert(gameState)).thenReturn(gameStateDto);
        assertThat(fixture.view(game, null)).isEqualTo(gameStateDto);
    }

    @Test
    void view_WhenAPlayerIdIsGiven_ThenLimitsTheResult(SeededRng random) {
        when(gameStateFactory.create(game.getGameStateData())).thenReturn(gameState);
        var players = List.of(alice, bob, charlie, dave);
        when(gameState.getPlayers()).thenReturn(players);
        var player = random.pick(players);
        when(gameState.limitedTo(player)).thenReturn(limitedCopy);
        when(gameStateDtoConverter.convert(limitedCopy)).thenReturn(gameStateDto);
        assertThat(fixture.view(game, new PlayerId(players.indexOf(player)))).isEqualTo(gameStateDto);
    }

}
