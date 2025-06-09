package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptConstant;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class ResolverModule extends SimpleModule {

    private final Map<String, ScriptConstant> constants;

    ResolverModule(Ruleset source) {
        constants = Map.copyOf(source.constants().collect(Collectors.toMap(Object::toString, Function.identity())));
        visit(Declaration.class);
        visit(Tag.class);
        visit(WeaponTarget.class);
    }

    private <T> void visit(Class<T> type) {
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
