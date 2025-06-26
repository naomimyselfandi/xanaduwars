package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.InvalidOperationException;

interface GameLobbyService {

    void join(Game game, Id<Account> accountId) throws ConflictException;

    void drop(Game game, Id<Account> accountId) throws ConflictException;

    void move(Game game, Id<Account> accountId, PlayerId playerId) throws ConflictException;

    void setChoices(Game game, PlayerId playerId, ChoiceDto choiceDto) throws InvalidOperationException;

}
