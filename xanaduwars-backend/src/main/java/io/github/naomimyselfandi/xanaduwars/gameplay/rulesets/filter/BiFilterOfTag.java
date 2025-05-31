package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Tag;
import jakarta.validation.constraints.NotNull;

record BiFilterOfTag<S, T extends Element>(@NotNull Tag tag) implements BiFilter<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return target.tags().contains(tag);
    }

    @Override
    public String toString() {
        return "tag." + tag;
    }

}
