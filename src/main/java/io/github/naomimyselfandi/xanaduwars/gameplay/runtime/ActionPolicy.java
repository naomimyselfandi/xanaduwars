package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;

import java.util.List;

interface ActionPolicy {

    <S extends Element> List<Action<? super S, ?>> actions(Ruleset ruleset, S user);

}
