package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

interface BiFilterWrapper<S, T> extends BiFilter<S, T> {

    BiFilter<?, ?> filter();

}
