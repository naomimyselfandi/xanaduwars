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
import org.jetbrains.annotations.Nullable;

/// The main service for working with games.
public interface GameService {

    /// Find the player ID corresponding to the given account in the given game, if any.
    @Nullable PlayerId findPlayerId(Id<Game> id, Id<Account> accountId) throws NoSuchEntityException;

    /// Get the player ID corresponding to the given account in the given game.
    PlayerId getPlayerId(Id<Game> id, Id<Account> accountId) throws ForbiddenOperationException, NoSuchEntityException;

    /// Get a game's metadata.
    GameDto getMetadata(Id<Game> id) throws NoSuchEntityException;

    /// Get a player's pre-game choices for some game.
    ChoiceDto getChoices(Id<Game> id, PlayerId playerId) throws NoSuchEntityException;

    /// Set a player's pre-game choices for some game.
    ChoiceDto setChoices(Id<Game> id, PlayerId playerId, ChoiceDto choiceDto)
            throws NoSuchEntityException, InvalidOperationException, ConflictException;

    /// View a game's game state as some player. If no player is given, the
    /// entire game state is returned without redaction.
    GameStateDto getState(Id<Game> id, @Nullable PlayerId playerId) throws NoSuchEntityException, ConflictException;

    /// Add an account to a game.
    GameDto join(Id<Game> id, Id<Account> accountId) throws NoSuchEntityException, ConflictException;

    /// Remove an account from a game.
    void drop(Id<Game> id, Id<Account> accountId) throws NoSuchEntityException, ConflictException;

    /// Set an account's player ID in a game.
    GameDto move(Id<Game> id, Id<Account> accountId, PlayerId playerId) throws NoSuchEntityException, ConflictException;

    /// Start a game.
    GameDto start(Id<Game> id) throws NoSuchEntityException, ConflictException;

    // Proper map support is coming later; for now, specifying the map data inline works.

    /// Create a game using an ad-hoc map.
    GameDto createAdHoc(Id<Account> accountId, MapDto map, Version version);

}
