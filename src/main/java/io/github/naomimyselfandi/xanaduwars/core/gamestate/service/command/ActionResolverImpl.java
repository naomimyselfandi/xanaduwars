package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.springframework.stereotype.Service;

@Service
class ActionResolverImpl implements ActionResolver {

    @Override
    public Action resolveAction(Element actor, Name name) throws ConflictException {
        return actor
                .getActions()
                .stream()
                .filter(it -> name.equals(it.getName()))
                .findFirst()
                .orElseThrow(() -> new ConflictException("Unknown action."));
    }

}
