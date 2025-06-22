package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
class CommandResolverImpl implements CommandResolver {

    private final ObjectResolver objectResolver;
    private final ItemResolver itemResolver;

    @Override
    public Command resolve(GameState gameState, CommandDto commandDto) throws ConflictException {
        var actor = objectResolver.resolveActor(gameState, commandDto.actor());
        var items = new ArrayList<Command.Item>();
        for (var itemDto : commandDto.items()) {
            items.add(itemResolver.resolveItem(gameState, itemDto, actor));
        }
        return new Command(actor, items);
    }

}
