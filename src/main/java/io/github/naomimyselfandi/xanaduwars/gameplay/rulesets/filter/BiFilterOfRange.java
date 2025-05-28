package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Range;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record BiFilterOfRange<S extends Node, T extends Node>(@NotNull @Valid Range range) implements BiFilter<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return target.distance(subject).filter(range).isPresent();
    }

    @Override
    @JsonValue
    public String toString() {
        return "range(%s)".formatted(range);
    }

}
