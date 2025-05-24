package io.github.naomimyselfandi.xanaduwars.core.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

record BiFilterAll<S, T>(@NotNull List<@NotNull @Valid BiFilter<S, T>> filters) implements BiFilter<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return filters.stream().allMatch(filter -> filter.test(subject, target));
    }

    @Override
    @JsonValue
    public String toString() {
        return "all(%s)".formatted(filters.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

}
