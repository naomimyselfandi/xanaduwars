package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.CopyMachine;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateDataService;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.GameRepository;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import io.github.naomimyselfandi.xanaduwars.game.entity.ReplayEntry;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class GameFactoryImpl implements GameFactory {

    private final CopyMachine copyMachine;
    private final GameRepository gameRepository;
    private final AccountRepository accountRepository;
    private final GameStateDataService gameStateDataService;

    @Override
    @Transactional
    @PreAuthorize("hasRole('DEVELOPER') && (@authService.id == #accountId)")
    public Game createAdHoc(Id<Account> accountId, MapDto map, Version version) {
        var account = accountRepository.findById(accountId).orElseThrow();
        var data = gameStateDataService.create(map, version);
        var game = new Game().setHost(account).setGameStateData(data);
        game.getReplay().add(new ReplayEntry().setSnapshot(copyMachine.copy(data)));
        game.getPlayerSlots().put(new PlayerId(0), new PlayerSlot().setAccount(account));
        return gameRepository.save(game);
    }

}
