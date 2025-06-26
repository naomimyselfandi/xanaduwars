package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.jetbrains.annotations.Nullable;

interface PlayerIdService {

    @Nullable PlayerId findPlayerId(Game game, Id<Account> accountId);

}
