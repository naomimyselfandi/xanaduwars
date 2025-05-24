package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.ActionException;
import io.github.naomimyselfandi.xanaduwars.core.ActionItem;
import io.github.naomimyselfandi.xanaduwars.core.Element;

import java.util.List;

interface ActionExecutor {

    <S extends Element> void execute(List<? extends ActionItem<S, ?>> items, S user) throws ActionException;

}
