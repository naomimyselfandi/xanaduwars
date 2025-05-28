package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RequiredArgsConstructor
final class RulesetModule extends SimpleModule {

    private final Ruleset ruleset;

    @Override
    public void setupModule(SetupContext context) {
        setupJavaClass(Type.class);
        super.setupModule(context);
    }

    private <T> void setupJavaClass(Class<T> javaClass) {
        addDeserializer(javaClass, new TypeDeserializer<>(javaClass));
        addKeyDeserializer(javaClass, new TypeKeyDeserializer(javaClass));
        if (javaClass.isSealed()) {
            for (var subclass : javaClass.getPermittedSubclasses()) {
                setupJavaClass(subclass);
            }
        }
    }

    private final class TypeDeserializer<T> extends StdDeserializer<T> {

        private final Class<T> javaClass;

        TypeDeserializer(Class<T> javaClass) {
            super(javaClass);
            this.javaClass = javaClass;
        }

        @Override
        public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return find(javaClass, parser.getValueAsString(), parser);
        }

    }

    @RequiredArgsConstructor
    private final class TypeKeyDeserializer extends KeyDeserializer {

        private final Class<?> javaClass;

        @Override
        public Object deserializeKey(String key, DeserializationContext context) throws IOException {
            return find(javaClass, key, context.getParser());
        }

    }

    private <T> T find(Class<T> javaClass, String key, JsonParser parser) throws IOException {
        Predicate<Type> predicate = type -> type.name().text().equals(key);
        return types()
                .filter(predicate)
                .filter(javaClass::isInstance)
                .map(javaClass::cast)
                .findFirst()
                .orElseThrow(() -> {
                    var message = types().anyMatch(predicate)
                            ? "'%s' is not a %s.".formatted(key, javaClass.getSimpleName())
                            : "Unknown %s '%s'.".formatted(javaClass.getSimpleName(), key);
                    return new JsonMappingException(parser, message);
                });
    }

    private Stream<Type> types() {
        return Stream.of(
                ruleset.commanders(),
                ruleset.spellTypes(),
                ruleset.tileTypes(),
                ruleset.unitTypes()
        ).flatMap(List::stream);
    }

}
