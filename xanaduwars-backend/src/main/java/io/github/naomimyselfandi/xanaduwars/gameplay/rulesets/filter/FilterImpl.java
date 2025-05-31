package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

record FilterImpl<T>(@NotNull @Valid BiFilter<T, T> filter) implements Filter<T> {

    @JsonIgnore
    @AssertTrue
    boolean isAppropriate() {
        return isAppropriate(filter);
    }

    @Override
    public boolean test(T input) {
        return filter.test(input, input);
    }

    @Override
    @JsonValue
    public String toString() {
        return filter.toString();
    }

    @SuppressWarnings("DuplicateBranchesInSwitch" /* Merging the false branches confuses the coverage report */)
    private static boolean isAppropriate(BiFilter<?, ?> filter) {
        return switch (filter) {
            case BiFilterAll<?, ?> all -> all.filters().stream().allMatch(FilterImpl::isAppropriate);
            case BiFilterAny<?, ?> any -> any.filters().stream().allMatch(FilterImpl::isAppropriate);
            case BiFilterWrapper<?, ?> wrapper -> isAppropriate(wrapper.filter());
            case BiFilterOfIff<?, ?> _ -> false;
            case BiFilterOfRange<?, ?> _ -> false;
            case BiFilterOfSubject<?, ?> _ -> false;
            default -> true;
        };
    }

}
