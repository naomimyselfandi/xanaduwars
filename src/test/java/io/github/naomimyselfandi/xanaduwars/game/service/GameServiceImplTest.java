package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.GameStateDto;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.game.dto.GameDto;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameServiceImplTest {

    private Id<Game> gameId;

    private Id<Account> accountId;

    private PlayerId playerId;

    private Game game;

    private GameDto gameDto;

    private ChoiceDto choiceDto;

    private GameStateDto gameStateDto;

    private MapDto mapDto;

    private Version version;

    @Mock
    private GameViewer gameViewer;

    @Mock
    private GameStarter gameStarter;

    @Mock
    private GameFetcher gameFetcher;

    @Mock
    private GameFactory gameFactory;

    @Mock
    private PlayerIdService playerIdService;

    @Mock
    private GameLobbyService gameLobbyService;

    @InjectMocks
    private GameServiceImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        gameId = random.get();
        accountId = random.get();
        playerId = random.get();
        game = random.get();
        gameDto = random.get();
        choiceDto = random.get();
        gameStateDto = random.get();
        mapDto = random.get();
        version = random.get();
    }

    @Test
    void findPlayerId() throws NoSuchEntityException {
        when(gameFetcher.fetch(gameId)).thenReturn(game);
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(playerId);
        assertThat(fixture.findPlayerId(gameId, accountId)).isEqualTo(playerId);
    }

    @Test
    void getPlayerId() throws ForbiddenOperationException, NoSuchEntityException {
        when(gameFetcher.fetch(gameId)).thenReturn(game);
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(playerId);
        assertThat(fixture.getPlayerId(gameId, accountId)).isEqualTo(playerId);
    }

    @Test
    void getPlayerId_WhenThePlayerIdDoesNotExist_ThenThrows() throws NoSuchEntityException {
        when(gameFetcher.fetch(gameId)).thenReturn(game);
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(null);
        assertThatThrownBy(() -> fixture.getPlayerId(gameId, accountId))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void getMetadata() throws NoSuchEntityException {
        when(gameFetcher.fetch(gameId)).thenReturn(game);
        when(gameViewer.getMetadata(game)).thenReturn(gameDto);
        assertThat(fixture.getMetadata(gameId)).isEqualTo(gameDto);
    }

    @Test
    void getChoices() throws NoSuchEntityException {
        when(gameFetcher.fetch(gameId)).thenReturn(game);
        when(gameViewer.getChoices(game, playerId)).thenReturn(choiceDto);
        assertThat(fixture.getChoices(gameId, playerId)).isEqualTo(choiceDto);
    }

    @Test
    void setChoices() throws ConflictException, InvalidOperationException, NoSuchEntityException {
        when(gameFetcher.fetchPending(gameId)).thenReturn(game);
        assertThat(fixture.setChoices(gameId, playerId, choiceDto)).isEqualTo(choiceDto);
        verify(gameLobbyService).setChoices(game, playerId, choiceDto);
    }

    @Test
    void getState() throws ConflictException, NoSuchEntityException {
        when(gameFetcher.fetchOngoing(gameId)).thenReturn(game);
        when(gameViewer.view(game, playerId)).thenReturn(gameStateDto);
        assertThat(fixture.getState(gameId, playerId)).isEqualTo(gameStateDto);
    }

    @Test
    void join() throws ConflictException, NoSuchEntityException {
        when(gameFetcher.fetchPending(gameId)).thenReturn(game);
        when(gameViewer.getMetadata(game)).thenReturn(gameDto);
        assertThat(fixture.join(gameId, accountId)).isEqualTo(gameDto);
        var inOrder = inOrder(gameLobbyService, gameViewer);
        inOrder.verify(gameLobbyService).join(game, accountId);
        inOrder.verify(gameViewer).getMetadata(game);
    }

    @Test
    void drop() throws ConflictException, NoSuchEntityException {
        when(gameFetcher.fetchPending(gameId)).thenReturn(game);
        fixture.drop(gameId, accountId);
        verify(gameLobbyService).drop(game, accountId);
    }

    @Test
    void move() throws ConflictException, NoSuchEntityException {
        when(gameFetcher.fetchPending(gameId)).thenReturn(game);
        when(gameViewer.getMetadata(game)).thenReturn(gameDto);
        assertThat(fixture.move(gameId, accountId, playerId)).isEqualTo(gameDto);
        var inOrder = inOrder(gameLobbyService, gameViewer);
        inOrder.verify(gameLobbyService).move(game, accountId, playerId);
        inOrder.verify(gameViewer).getMetadata(game);
    }

    @Test
    void start() throws ConflictException, NoSuchEntityException {
        when(gameFetcher.fetchPending(gameId)).thenReturn(game);
        when(gameViewer.getMetadata(game)).thenReturn(gameDto);
        assertThat(fixture.start(gameId)).isEqualTo(gameDto);
        var inOrder = inOrder(gameStarter, gameViewer);
        inOrder.verify(gameStarter).start(game);
        inOrder.verify(gameViewer).getMetadata(game);
    }

    @Test
    void createAdHoc() {
        when(gameViewer.getMetadata(game)).thenReturn(gameDto);
        when(gameFactory.createAdHoc(accountId, mapDto, version)).thenReturn(game);
        assertThat(fixture.createAdHoc(accountId, mapDto, version)).isEqualTo(gameDto);
    }

}
