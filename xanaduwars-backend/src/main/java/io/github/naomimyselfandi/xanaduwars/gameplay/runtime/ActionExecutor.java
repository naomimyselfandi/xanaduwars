package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.ActionException;
import io.github.naomimyselfandi.xanaduwars.gameplay.ActionItem;
import io.github.naomimyselfandi.xanaduwars.gameplay.Actor;
import io.github.naomimyselfandi.xanaduwars.gameplay.Element;

import java.util.List;

interface ActionExecutor {

    <S extends Actor> void execute(List<? extends ActionItem<S, ?>> items, S user) throws ActionException;

}
