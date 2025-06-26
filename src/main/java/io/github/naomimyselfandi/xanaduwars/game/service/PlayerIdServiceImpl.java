package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class PlayerIdServiceImpl implements PlayerIdService {

    @Override
    public @Nullable PlayerId findPlayerId(Game game, Id<Account> accountId) {
        return game
                .getPlayerSlots()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getAccount().getId().equals(accountId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

}
