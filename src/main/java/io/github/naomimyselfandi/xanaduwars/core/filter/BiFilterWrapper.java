package io.github.naomimyselfandi.xanaduwars.core.filter;

interface BiFilterWrapper<S, T> extends BiFilter<S, T> {

    BiFilter<?, ?> filter();

}
