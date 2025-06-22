package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.ActionGroupQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.ActionStatusQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.CleanupEvent;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CommandProcessorHelperImpl implements CommandProcessorHelper {

    private static final CleanupEvent CLEANUP = new CleanupEvent();

    private final CommandResolver commandResolver;
    private final ItemProcessor itemProcessor;

    @Override
    public Result process(GameState gameState, CommandDto commandDto) throws ConflictException {
        var command = commandResolver.resolve(gameState, commandDto);
        var actor = command.actor();
        var items = command.items();
        var actions = items.stream().map(Command.Item::action).toList();
        if (gameState.evaluate(new ActionStatusQuery(actor)) instanceof Result.Fail fail) {
            return fail;
        }
        if (gameState.evaluate(new ActionGroupQuery(actor, actions)) instanceof Result.Fail fail) {
            return fail;
        }
        for (var item : items) {
            if (itemProcessor.process(actor, item) instanceof Result.Fail fail) {
                return fail;
            }
        }
        gameState.evaluate(CLEANUP);
        return Result.okay();
    }

}
