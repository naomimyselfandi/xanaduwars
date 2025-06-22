package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CommandProcessorImpl implements CommandProcessor {

    private final CommandProcessorHelper commandProcessorHelper;

    @Override
    public Result process(GameState gameState, CommandDto command) throws ConflictException {
        // If a failure does not involve hidden information, we need to promote
        // it to an exception. If the game state is a limited copy, we know all
        // hidden information has already been removed, so any failure should
        // be promoted. Otherwise, we first process the command in a limited
        // copy, process the command in the copy, and promote any failures that
        // occur; any failures processing the command in the original game state
        // after that cannot involve hidden information, so we leave them alone.
        if (gameState.isLimitedCopy()) {
            return switch (commandProcessorHelper.process(gameState, command)) {
                case Result.Okay okay -> okay;
                case Result.Fail fail -> throw new ConflictException(fail.message());
            };
        } else {
            process(gameState.limitedTo(gameState.getActivePlayer()), command);
            return commandProcessorHelper.process(gameState, command);
        }
    }

}
