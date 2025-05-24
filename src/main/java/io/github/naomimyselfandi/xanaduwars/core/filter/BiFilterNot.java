package io.github.naomimyselfandi.xanaduwars.core.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record BiFilterNot<S, T>(@Override @NotNull @Valid BiFilter<S, T> filter) implements BiFilterWrapper<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return !filter.test(subject, target);
    }

    @Override
    @JsonValue
    public String toString() {
        return "!" + filter;
    }

}
