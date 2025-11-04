package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;

import java.util.stream.Stream;

/// A specification of an ability's target.
@JsonDeserialize(using = TargetDeserializer.class)
interface Target<T, P> {

    T unpack(Actor actor, JsonNode target) throws CommandException;

    boolean validate(Actor actor, Object target);

    Stream<P> propose(Actor actor);

    JsonNode pack(P proposal);

}
