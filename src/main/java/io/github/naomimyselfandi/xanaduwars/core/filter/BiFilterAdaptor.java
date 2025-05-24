package io.github.naomimyselfandi.xanaduwars.core.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record BiFilterAdaptor<S, T, TT extends T>(
        @NotNull Class<TT> javaClass,
        @Override @NotNull @Valid BiFilter<S, TT> filter
) implements BiFilterWrapper<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return javaClass.isInstance(target) && filter.test(subject, javaClass.cast(target));
    }

    @Override
    @JsonValue
    public String toString() {
        return "as(%s).%s".formatted(javaClass.getSimpleName(), filter);
    }

}
