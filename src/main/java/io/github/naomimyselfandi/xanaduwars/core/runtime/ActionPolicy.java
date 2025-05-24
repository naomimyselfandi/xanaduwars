package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.Action;
import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.Ruleset;

import java.util.List;

interface ActionPolicy {

    <S extends Element> List<Action<? super S, ?>> actions(Ruleset ruleset, S user);

}
