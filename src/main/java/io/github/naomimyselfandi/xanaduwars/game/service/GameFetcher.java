package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;

interface GameFetcher {

    Game fetch(Id<Game> id) throws NoSuchEntityException;

    Game fetchPending(Id<Game> id) throws NoSuchEntityException, ConflictException;

    Game fetchOngoing(Id<Game> id) throws NoSuchEntityException, ConflictException;

}
