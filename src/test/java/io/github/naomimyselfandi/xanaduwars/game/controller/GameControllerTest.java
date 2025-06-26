package io.github.naomimyselfandi.xanaduwars.game.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.GameStateDto;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.VersionService;
import io.github.naomimyselfandi.xanaduwars.game.dto.GameDto;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.service.GameService;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameControllerTest {

    private Id<Game> id;

    private Id<Account> accountId;

    private PlayerId playerId;

    private GameDto gameDto;

    private GameStateDto gameStateDto;

    private ChoiceDto choiceDto, anotherChoiceDto;

    @Mock
    private GameService gameService;

    @Mock
    private VersionService versionService;

    @InjectMocks
    private GameController fixture;

    @BeforeEach
    void setup(SeededRng random) {
        id = random.get();
        accountId = random.get();
        playerId = random.get();
        gameDto = random.get();
        gameStateDto = random.get();
        choiceDto = random.get();
        anotherChoiceDto = random.not(choiceDto);
    }

    @Test
    void getMetadata() throws NoSuchEntityException {
        when(gameService.getMetadata(id)).thenReturn(gameDto);
        assertThat(fixture.getMetadata(id)).isEqualTo(ResponseEntity.ok(gameDto));
    }

    @Test
    void getChoices() throws NoSuchEntityException, ForbiddenOperationException {
        when(gameService.getPlayerId(id, accountId)).thenReturn(playerId);
        when(gameService.getChoices(id, playerId)).thenReturn(choiceDto);
        assertThat(fixture.getChoices(id, accountId)).isEqualTo(ResponseEntity.ok(choiceDto));
    }

    @Test
    void setChoices() throws NoSuchEntityException, ForbiddenOperationException, InvalidOperationException, ConflictException {
        when(gameService.getPlayerId(id, accountId)).thenReturn(playerId);
        when(gameService.setChoices(id, playerId, choiceDto)).thenReturn(anotherChoiceDto);
        assertThat(fixture.setChoices(id, accountId, choiceDto)).isEqualTo(ResponseEntity.ok(anotherChoiceDto));
    }

    @Test
    void getState_WhenNoPlayerIdIsGiven_ThenResolvesItAutomatically() throws NoSuchEntityException, ConflictException {
        when(gameService.getState(id, playerId)).thenReturn(gameStateDto);
        when(gameService.findPlayerId(id, accountId)).thenReturn(playerId);
        assertThat(fixture.getState(id, accountId, null)).isEqualTo(ResponseEntity.ok(gameStateDto));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void getState_WhenAPlayerIdIsGiven_ThenUsesIt(int playerId) throws NoSuchEntityException, ConflictException {
        when(gameService.getState(id, new PlayerId(playerId))).thenReturn(gameStateDto);
        assertThat(fixture.getState(id, accountId, playerId)).isEqualTo(ResponseEntity.ok(gameStateDto));
    }

    @Test
    void getState_WhenANegativePlayerIdIsGiven_ThenRequestsTheWholeState()
            throws NoSuchEntityException, ConflictException {
        when(gameService.getState(id, null)).thenReturn(gameStateDto);
        assertThat(fixture.getState(id, accountId, -1)).isEqualTo(ResponseEntity.ok(gameStateDto));
        verify(gameService, never()).findPlayerId(id, accountId);
    }

    @Test
    void join() throws ConflictException, NoSuchEntityException {
        when(gameService.join(id, accountId)).thenReturn(gameDto);
        assertThat(fixture.join(id, accountId)).isEqualTo(ResponseEntity.ok(gameDto));
    }

    @Test
    void drop() throws ConflictException, NoSuchEntityException {
        assertThat(fixture.drop(id, accountId)).isEqualTo(ResponseEntity.noContent().build());
        verify(gameService).drop(id, accountId);
    }

    @Test
    void kick() throws ConflictException, NoSuchEntityException {
        when(gameService.getMetadata(id)).thenReturn(gameDto);
        assertThat(fixture.kick(id, accountId)).isEqualTo(ResponseEntity.ok(gameDto));
        var inOrder = inOrder(gameService);
        inOrder.verify(gameService).drop(id, accountId);
        inOrder.verify(gameService).getMetadata(id);
    }

    @Test
    void start() throws ConflictException, NoSuchEntityException {
        when(gameService.start(id)).thenReturn(gameDto);
        assertThat(fixture.start(id)).isEqualTo(ResponseEntity.ok(gameDto));
    }

    @Test
    void createAdHoc(SeededRng random) {
        var map = random.<MapDto>get();
        var version = random.<Version>get();
        when(gameService.createAdHoc(accountId, map, version)).thenReturn(gameDto);
        assertThat(fixture.createAdHoc(accountId, map, version)).isEqualTo(ResponseEntity.ok(gameDto));
    }

    @Test
    void createAdHoc_WhenNoVersionIsGiven_ThenUsesTheCurrentVersion(SeededRng random) {
        var map = random.<MapDto>get();
        var version = random.<Version>get();
        when(versionService.current()).thenReturn(version);
        when(gameService.createAdHoc(accountId, map, version)).thenReturn(gameDto);
        assertThat(fixture.createAdHoc(accountId, map, null)).isEqualTo(ResponseEntity.ok(gameDto));
    }

}
