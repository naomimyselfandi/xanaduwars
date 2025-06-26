package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.ChosenSpells;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.ChoiceService;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.InvalidOperationException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameLobbyServiceImplTest {

    private Game game;

    private Id<Account> accountId;

    private Account account, anotherAccount, yetAnotherAccount;

    private ChoiceDto choiceDto;

    @Mock
    private GameState gameState;

    @Mock
    private ChoiceService choiceService;

    @Mock
    private PlayerIdService playerIdService;

    @Mock
    private GameStateFactory gameStateFactory;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private GameLobbyServiceImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        game = random.get();
        game.setPlayerSlots(new TreeMap<>());
        game.getGameStateData().setPlayers(List.of(random.get(), random.get()));
        game.setStatus(Game.Status.PENDING);
        accountId = random.get();
        account = random.get();
        anotherAccount = random.get();
        yetAnotherAccount = random.get();
        choiceDto = random.get();
    }

    @Test
    void join() throws ConflictException {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        fixture.join(game, accountId);
        assertThat(game.getPlayerSlots())
                .hasSize(2)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount))
                .containsEntry(new PlayerId(1), new PlayerSlot().setAccount(account));
    }

    @Test
    void join_WhenAlreadyJoined_ThenThrows(SeededRng random) {
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(random.get());
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        assertThatThrownBy(() -> fixture.join(game, accountId))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Already in that game.");
        assertThat(game.getPlayerSlots())
                .hasSize(1)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
    }

    @Test
    void join_WhenNoAvailableSlots_ThenThrows() {
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        game.getPlayerSlots().put(new PlayerId(1), new PlayerSlot().setAccount(yetAnotherAccount));
        assertThatThrownBy(() -> fixture.join(game, accountId))
                .isInstanceOf(ConflictException.class)
                .hasMessage("No open player slot.");
        assertThat(game.getPlayerSlots())
                .hasSize(2)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount))
                .containsEntry(new PlayerId(1), new PlayerSlot().setAccount(yetAnotherAccount));
    }

    @Test
    void drop() throws ConflictException {
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(new PlayerId(1));
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        game.getPlayerSlots().put(new PlayerId(1), new PlayerSlot().setAccount(account));
        game.setHost(anotherAccount);
        fixture.drop(game, accountId);
        assertThat(game.getStatus()).isEqualTo(Game.Status.PENDING);
        assertThat(game.getPlayerSlots())
                .hasSize(1)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        assertThat(game.getGameStateData().getPlayers().get(1))
                .returns(null, PlayerData::getCommanderId)
                .returns(new ChosenSpells(), PlayerData::getChosenSpells);
    }

    @Test
    void drop_WhenNotInTheGame_ThenThrows() {
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(null);
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        game.getPlayerSlots().put(new PlayerId(1), new PlayerSlot().setAccount(yetAnotherAccount));
        assertThatThrownBy(() -> fixture.drop(game, accountId))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Not in that game.");
        assertThat(game.getPlayerSlots())
                .hasSize(2)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount))
                .containsEntry(new PlayerId(1), new PlayerSlot().setAccount(yetAnotherAccount));
    }

    @Test
    void drop_WhenTheHostDrops_ThenTheGameIsCanceled() throws ConflictException {
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(new PlayerId(1));
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        game.getPlayerSlots().put(new PlayerId(1), new PlayerSlot().setAccount(account));
        game.setHost(account.setId(accountId));
        fixture.drop(game, accountId);
        assertThat(game.getStatus()).isEqualTo(Game.Status.CANCELED);
        assertThat(game.getPlayerSlots())
                .hasSize(1)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        assertThat(game.getGameStateData().getPlayers().get(1))
                .returns(null, PlayerData::getCommanderId)
                .returns(new ChosenSpells(), PlayerData::getChosenSpells);
    }

    @Test
    void move() throws ConflictException {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(new PlayerId(0));
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(account));
        game.setHost(account.setId(accountId));
        fixture.move(game, accountId, new PlayerId(1));
        assertThat(game.getStatus()).isEqualTo(Game.Status.PENDING);
        assertThat(game.getPlayerSlots())
                .hasSize(1)
                .containsEntry(new PlayerId(1), new PlayerSlot().setAccount(account));
        assertThat(game.getGameStateData().getPlayers().getFirst())
                .returns(null, PlayerData::getCommanderId)
                .returns(new ChosenSpells(), PlayerData::getChosenSpells);
    }

    @Test
    void move_WhenNotInTheGame_ThenThrows(SeededRng random) {
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(null);
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        game.getPlayerSlots().put(new PlayerId(1), new PlayerSlot().setAccount(yetAnotherAccount));
        assertThatThrownBy(() -> fixture.move(game, accountId, random.get()))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Not in that game.");
        assertThat(game.getPlayerSlots())
                .hasSize(2)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount))
                .containsEntry(new PlayerId(1), new PlayerSlot().setAccount(yetAnotherAccount));
    }

    @Test
    void move_WhenTheSlotIsFull_ThenThrows() {
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(new PlayerId(1));
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        game.getPlayerSlots().put(new PlayerId(1), new PlayerSlot().setAccount(account));
        assertThatThrownBy(() -> fixture.move(game, accountId, new PlayerId(0)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("That slot is already full.");
        assertThat(game.getPlayerSlots())
                .hasSize(2)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount))
                .containsEntry(new PlayerId(1), new PlayerSlot().setAccount(account));
    }

    @Test
    void move_WhenTheSlotDoesNotExist_ThenThrows() {
        when(playerIdService.findPlayerId(game, accountId)).thenReturn(new PlayerId(1));
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount));
        game.getPlayerSlots().put(new PlayerId(1), new PlayerSlot().setAccount(account));
        assertThatThrownBy(() -> fixture.move(game, accountId, new PlayerId(4)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Undefined slot.");
        assertThat(game.getPlayerSlots())
                .hasSize(2)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(anotherAccount))
                .containsEntry(new PlayerId(1), new PlayerSlot().setAccount(account));
    }

    @Test
    void setChoices(SeededRng random) throws InvalidOperationException {
        var playerId = random.<PlayerId>get();
        when(gameStateFactory.create(game.getGameStateData())).thenReturn(gameState);
        fixture.setChoices(game, playerId, choiceDto);
        verify(choiceService).setChoices(gameState, playerId, choiceDto);
    }

    private enum Operation {JOIN, DROP, MOVE, CHOOSE}

    @EnumSource
    @ParameterizedTest
    void whenTheGameIsNotPending_ThenThrows(Operation operation, SeededRng random) {
        game.setStatus(random.not(Game.Status.PENDING));
        ThrowableAssert.ThrowingCallable callback = switch (operation) {
            case JOIN -> () -> fixture.join(game, accountId);
            case DROP -> () -> fixture.drop(game, accountId);
            case MOVE -> () -> fixture.move(game, accountId, random.get());
            case CHOOSE -> () -> fixture.setChoices(game, random.get(), choiceDto);
        };
        assertThatThrownBy(callback).isInstanceOf(IllegalStateException.class);
    }

}
