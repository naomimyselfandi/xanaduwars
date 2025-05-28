package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

record BiFilterOfSubject<S, T>(BiFilter<T, S> filter) implements BiFilter<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return filter.test(target, subject);
    }

    @Override
    public String toString() {
        return "@" + filter;
    }

}
