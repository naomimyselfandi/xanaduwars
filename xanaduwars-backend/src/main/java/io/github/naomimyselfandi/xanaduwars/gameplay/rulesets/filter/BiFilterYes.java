package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

record BiFilterYes<S, T>() implements BiFilter<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return true;
    }

    @Override
    public String toString() {
        return "*";
    }

}
