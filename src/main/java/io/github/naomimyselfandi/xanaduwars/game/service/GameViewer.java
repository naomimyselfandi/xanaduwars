package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.GameStateDto;
import io.github.naomimyselfandi.xanaduwars.game.dto.GameDto;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import org.jetbrains.annotations.Nullable;

interface GameViewer {

    GameDto getMetadata(Game game);

    ChoiceDto getChoices(Game game, PlayerId playerId);

    GameStateDto view(Game game, @Nullable PlayerId playerId);

}
