package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;

record BiFilterOfAttackRange<T extends Node>() implements BiFilter<Unit, T> {

    @Override
    public boolean test(Unit subject, T target) {
        return subject.distance(target).filter(subject.range()).isPresent();
    }

    @Override
    @JsonValue
    public String toString() {
        return "range(attack)";
    }

}
