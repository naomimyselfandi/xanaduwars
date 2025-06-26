package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.GameRepository;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class GameFetcherImpl implements GameFetcher {

    private final GameRepository gameRepository;

    @Override
    public Game fetch(Id<Game> id) throws NoSuchEntityException {
        return gameRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }

    @Override
    public Game fetchPending(Id<Game> id) throws NoSuchEntityException, ConflictException {
        var game = fetch(id);
        return switch (game.getStatus()) {
            case PENDING -> game;
            case ONGOING -> throw new ConflictException("The game has already started.");
            case FINISHED, CANCELED -> throw new ConflictException("The game has already ended.");
        };
    }

    @Override
    public Game fetchOngoing(Id<Game> id) throws NoSuchEntityException, ConflictException {
        var game = fetch(id);
        return switch (game.getStatus()) {
            case ONGOING -> game;
            case PENDING -> throw new ConflictException("The game has not started yet.");
            case FINISHED, CANCELED -> throw new ConflictException("The game has already ended.");
        };
    }

}
