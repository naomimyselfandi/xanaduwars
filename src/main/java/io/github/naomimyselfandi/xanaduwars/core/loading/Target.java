package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/// A specification of an ability's target.
@JsonDeserialize(using = TargetDeserializer.class)
interface Target<T> {

    @Nullable T unpack(Actor actor, JsonNode target);

    boolean validate(Actor actor, Object target);

    Stream<T> propose(Actor actor);

    JsonNode pack(Object proposal);

}
