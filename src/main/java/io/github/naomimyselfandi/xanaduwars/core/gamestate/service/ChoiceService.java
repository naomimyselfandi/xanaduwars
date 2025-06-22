package io.github.naomimyselfandi.xanaduwars.core.gamestate.service;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.util.InvalidOperationException;

/// A service that manages players' pre-game choices.
public interface ChoiceService {

    /// Get a player's pre-game choices in some game.
    ChoiceDto getChoices(GameState gameState, PlayerId playerId);

    /// Set a player's pre-game choices in some game.
    void setChoices(GameState gameState, PlayerId playerId, ChoiceDto choices) throws InvalidOperationException;

}
