package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandSequence;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CommandServiceImpl implements CommandService {

    private final CopyMachine copyMachine;

    @Override
    public void submit(GameState gameState, CommandSequence commandSequence) throws CommandException {
        // If the game state isn't redacted, evaluate the command in a redacted
        // copy first. If it fails for any reason, even partially, stop without
        // doing anything. This prevents an exploit where a command that may be
        // interrupted is followed with an invalid command. If the first command
        // is interrupted, the second isn't attempted, so the sequence succeeds.
        // If the first command fails, the sequence is rejected, giving the user
        // additional information without changing the game state. (This could
        // be used, for example, to look for hidden enemies in two tiles with a
        // unit that can reach both of them, but not in the same move.)
        if (!gameState.isRedacted()) {
            var copy = copyMachine.createRedactedCopy(gameState, gameState.getActivePlayer());
            if (!commandSequence.submit(copy)) {
                throw new CommandException("Invalid command.");
            }
        }
        commandSequence.submit(gameState);
    }

}
