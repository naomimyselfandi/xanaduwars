package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandItemDto;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;

interface ItemResolver {

    Command.Item resolveItem(GameState gameState, CommandItemDto dto, Element actor) throws ConflictException;

}
