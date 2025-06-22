package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandItemDto;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
class ItemResolverImpl implements ItemResolver {

    private final ActionResolver actionResolver;
    private final ObjectResolver objectResolver;

    @Override
    public Command.Item resolveItem(GameState gameState, CommandItemDto dto, Element actor) throws ConflictException {
        var action = actionResolver.resolveAction(actor, dto.name());
        var targetSpecs = action.getTargets();
        var list = dto.targets();
        var count = targetSpecs.size();
        if (count != list.size()) {
            throw new ConflictException("Wrong number of targets.");
        } else {
            var targets = new ArrayList<>(count);
            for (var i = 0; i < count; i++) {
                targets.add(objectResolver.resolveTarget(gameState, list.get(i), targetSpecs.get(i)));
            }
            return new Command.Item(action, targets);
        }
    }

}
