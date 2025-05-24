package io.github.naomimyselfandi.xanaduwars.core;

import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
class RuleHelper {

    @SuppressWarnings("unchecked")
    <Q extends Query<V>, V> Class<Q> queryType(Rule<Q, V> rule) {
        // The Jackson devs already did the heavy lifting for us.
        return (Class<Q>) TypeFactory
                .defaultInstance()
                .constructType(rule.getClass())
                .findTypeParameters(Rule.class)[0]
                .getRawClass();
    }

}
