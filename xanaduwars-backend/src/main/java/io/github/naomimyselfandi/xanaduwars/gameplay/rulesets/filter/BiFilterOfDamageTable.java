package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;

record BiFilterOfDamageTable<T extends Node>() implements BiFilter<Unit, T> {

    @Override
    public boolean test(Unit subject, T target) {
        return subject.damageTable().containsKey(target.type());
    }

    @Override
    @JsonValue
    public String toString() {
        return "hasDamageValue";
    }

}
