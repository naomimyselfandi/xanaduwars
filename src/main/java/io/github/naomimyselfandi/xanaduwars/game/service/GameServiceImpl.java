package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.GameStateDto;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.game.dto.GameDto;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import io.github.naomimyselfandi.xanaduwars.util.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class GameServiceImpl implements GameService {

    private final GameViewer gameViewer;
    private final GameStarter gameStarter;
    private final GameFetcher gameFetcher;
    private final GameFactory gameFactory;
    private final PlayerIdService playerIdService;
    private final GameLobbyService gameLobbyService;

    @Override
    public @Nullable PlayerId findPlayerId(Id<Game> id, Id<Account> accountId) throws NoSuchEntityException {
        return playerIdService.findPlayerId(gameFetcher.fetch(id), accountId);
    }

    @Override
    public PlayerId getPlayerId(Id<Game> id, Id<Account> accountId)
            throws ForbiddenOperationException, NoSuchEntityException {
        var result = findPlayerId(id, accountId);
        if (result == null) throw new ForbiddenOperationException();
        return result;
    }

    @Override
    public GameDto getMetadata(Id<Game> id) throws NoSuchEntityException {
        return gameViewer.getMetadata(gameFetcher.fetch(id));
    }

    @Override
    public ChoiceDto getChoices(Id<Game> id, PlayerId playerId) throws NoSuchEntityException {
        var game = gameFetcher.fetch(id);
        return gameViewer.getChoices(game, playerId);
    }

    @Override
    public ChoiceDto setChoices(Id<Game> id, PlayerId playerId, ChoiceDto choiceDto)
            throws NoSuchEntityException, InvalidOperationException, ConflictException {
        var game = gameFetcher.fetchPending(id);
        gameLobbyService.setChoices(game, playerId, choiceDto);
        return choiceDto;
    }

    @Override
    public GameStateDto getState(Id<Game> id, @Nullable PlayerId playerId)
            throws NoSuchEntityException, ConflictException {
        return gameViewer.view(gameFetcher.fetchOngoing(id), playerId);
    }

    @Override
    public GameDto join(Id<Game> id, Id<Account> accountId) throws NoSuchEntityException, ConflictException {
        var game = gameFetcher.fetchPending(id);
        gameLobbyService.join(game, accountId);
        return gameViewer.getMetadata(game);
    }

    @Override
    public void drop(Id<Game> id, Id<Account> accountId) throws NoSuchEntityException, ConflictException {
        var game = gameFetcher.fetchPending(id);
        gameLobbyService.drop(game, accountId);
    }

    @Override
    public GameDto move(Id<Game> id, Id<Account> accountId, PlayerId playerId)
            throws NoSuchEntityException, ConflictException {
        var game = gameFetcher.fetchPending(id);
        gameLobbyService.move(game, accountId, playerId);
        return gameViewer.getMetadata(game);
    }

    @Override
    public GameDto start(Id<Game> id) throws NoSuchEntityException, ConflictException {
        var game = gameFetcher.fetchPending(id);
        gameStarter.start(game);
        return gameViewer.getMetadata(game);
    }

    @Override
    public GameDto createAdHoc(Id<Account> accountId, MapDto map, Version version) {
        var game = gameFactory.createAdHoc(accountId, map, version);
        return gameViewer.getMetadata(game);
    }

}
