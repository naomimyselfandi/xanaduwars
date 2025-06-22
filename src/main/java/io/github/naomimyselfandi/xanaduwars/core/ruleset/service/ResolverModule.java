package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.naomimyselfandi.xanaduwars.core.common.DamageKey;
import io.github.naomimyselfandi.xanaduwars.core.common.Tag;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetFilter;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Declaration;

import java.io.IOException;
import java.util.*;

class ResolverModule extends SimpleModule {

    private final Map<String, Object> constants;

    ResolverModule(Map<String, ?> constants) {
        var visitor = new Visitor();
        visitor.putAll(constants);
        visitor.visit(Action.class);
        visitor.visit(DamageKey.class);
        visitor.visit(Declaration.class);
        visitor.visit(Tag.class);
        visitor.visit(TargetFilter.class);
        this.constants = Map.copyOf(visitor);
    }

    private class Visitor extends HashMap<String, Object> {

        private final Set<Class<?>> visited = new HashSet<>();

        <T> void visit(Class<T> type) {
            if (!visited.add(type)) return;
            addDeserializer(type, new StdDeserializer<>(type) {
                @Override
                public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                    return resolve(type, parser.getValueAsString(), parser);
                }
            });
            addKeyDeserializer(type, new KeyDeserializer() {
                @Override
                public Object deserializeKey(String key, DeserializationContext context) throws IOException {
                    return resolve(type, key, context.getParser());
                }
            });
            if (type.isSealed()) {
                for (var subtype : type.getPermittedSubclasses()) {
                    visit(subtype);
                }
            }
        }

    }

    private <T> T resolve(Class<T> type, String key, JsonParser parser) throws JsonMappingException {
        var constant = constants.get(key);
        if (type.isInstance(constant)) {
            return type.cast(constant);
        } else if (constant != null) {
            var message = "'%s' is not a %s.".formatted(key, type.getSimpleName());
            throw new JsonMappingException(parser, message);
        } else {
            var message = "Unknown %s '%s'.".formatted(type.getSimpleName(), key);
            throw new JsonMappingException(parser, message);
        }
    }

}
