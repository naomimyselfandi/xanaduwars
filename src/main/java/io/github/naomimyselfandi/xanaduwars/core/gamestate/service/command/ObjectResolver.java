package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ActorRefDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.TargetRefDto;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;

interface ObjectResolver {

    Element resolveActor(GameState gameState, ActorRefDto reference) throws ConflictException;

    Object resolveTarget(GameState gameState, TargetRefDto reference, TargetSpec targetSpec) throws ConflictException;

}
