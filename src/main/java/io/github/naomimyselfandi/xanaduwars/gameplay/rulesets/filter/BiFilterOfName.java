package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import jakarta.validation.constraints.NotNull;

record BiFilterOfName<S, T extends Element>(@NotNull Name name) implements BiFilter<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return target.name().equals(name);
    }

    @Override
    public String toString() {
        return "name." + name;
    }

}
