package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;

import java.util.stream.Stream;

enum TargetOfNothing implements Target<Object> {

    NOTHING;

    @Override
    public Object unpack(Actor actor, JsonNode target) {
        return NOTHING;
    }

    @Override
    public boolean validate(Actor actor, Object target) {
        return true;
    }

    @Override
    public Stream<Object> propose(Actor actor) {
        return Stream.of(NOTHING);
    }

    @Override
    public JsonNode pack(Object proposal) {
        return NullNode.getInstance();
    }

}
