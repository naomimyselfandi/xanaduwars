package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

interface ItemProcessor {

    Result process(Element actor, Command.Item item);

}
