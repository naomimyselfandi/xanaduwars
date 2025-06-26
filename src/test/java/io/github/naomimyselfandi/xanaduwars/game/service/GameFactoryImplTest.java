package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.CopyMachine;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateDataService;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.GameRepository;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import io.github.naomimyselfandi.xanaduwars.game.entity.ReplayEntry;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameFactoryImplTest {

    private Id<Account> accountId;

    private Account account;

    private GameStateData gameStateData, copyStateData;

    private Game saved;

    private MapDto mapDto;

    private Version version;

    @Mock
    private CopyMachine copyMachine;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private GameStateDataService gameStateDataService;

    @InjectMocks
    private GameFactoryImpl fixture;

    @Captor
    private ArgumentCaptor<Game> captor;

    @BeforeEach
    void setup(SeededRng random) {
        accountId = random.get();
        account = random.get();
        gameStateData = random.get();
        copyStateData = random.not(gameStateData);
        saved = random.get();
        mapDto = random.get();
        version = random.get();
    }

    @Test
    void createAdHoc() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(gameStateDataService.create(mapDto, version)).thenReturn(gameStateData);
        when(gameRepository.save(any())).thenReturn(saved);
        when(copyMachine.copy(gameStateData)).thenReturn(copyStateData);
        assertThat(fixture.createAdHoc(accountId, mapDto, version)).isEqualTo(saved);
        verify(gameRepository).save(captor.capture());
        var game = captor.getValue();
        assertThat(game.getHost()).isEqualTo(account);
        assertThat(game.getGameStateData()).isEqualTo(gameStateData);
        assertThat(game.getPlayerSlots())
                .hasSize(1)
                .containsEntry(new PlayerId(0), new PlayerSlot().setAccount(account));
        assertThat(game.getReplay()).containsExactly(new ReplayEntry().setSnapshot(copyStateData));
    }

}
