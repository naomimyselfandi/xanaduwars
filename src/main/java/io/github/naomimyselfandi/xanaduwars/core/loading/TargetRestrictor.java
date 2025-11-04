package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;

import java.util.stream.Stream;

interface TargetRestrictor<T> extends Target<T> {

    Target<T> base();

    boolean validateFurther(Actor actor, T target);

    @Override
    default T unpack(Actor actor, JsonNode target) throws CommandException {
        return base().unpack(actor, target);
    }

    @Override
    @SuppressWarnings("unchecked")
    default boolean validate(Actor actor, Object target) {
        // unchecked cast OK since validate() throws or returns false for invalid targets
        return base().validate(actor, target) && validateFurther(actor, (T) target);
    }

    @Override
    default Stream<T> propose(Actor actor) {
        return base().propose(actor).filter(it -> validateFurther(actor, it));
    }

    @Override
    default JsonNode pack(Object proposal) {
        return base().pack(proposal);
    }

}
