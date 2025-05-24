package io.github.naomimyselfandi.xanaduwars.core.filter;

import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
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
