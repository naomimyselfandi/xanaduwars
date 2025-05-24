package io.github.naomimyselfandi.xanaduwars.core.filter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Filters {

    public <S, T> BiFilter<S, T> defaultBiFilter() {
        return new BiFilterYes<>();
    }

    public <T> Filter<T> defaultFilter() {
        return new FilterImpl<>(defaultBiFilter());
    }

}
