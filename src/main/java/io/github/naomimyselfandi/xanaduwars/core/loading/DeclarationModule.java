package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.naomimyselfandi.xanaduwars.core.message.MessageType;
import io.github.naomimyselfandi.xanaduwars.core.model.*;

import java.io.IOException;
import java.util.List;

/// A Jackson module that resolves declarations against a version.
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DeclarationModule extends SimpleModule {

    private static final List<Class<?>> TYPES = List.of(
            Ability.class,
            Commander.class,
            AbilityTag.class,
            TileTag.class,
            TileType.class,
            UnitSelector.class,
            UnitTag.class,
            UnitType.class,
            MessageType.class
    );

    /// Construct a Jackson module that resolves declarations against a version.
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public DeclarationModule(@JsonProperty("version") Version version) {
        for (var type : TYPES) {
            addDeclarationDeserializer(version, type);
        }
    }

    private <T> void addDeclarationDeserializer(Version version, Class<T> type) {
        var keyDeserializer = new KeyDeserializer() {
            @Override
            public Object deserializeKey(String name, DeserializationContext context) throws IOException {
                var candidate = version.lookup(name);
                if (type.isInstance(candidate)) {
                    return type.cast(candidate);
                } else {
                    var problem = (candidate == null) ? "Unknown" : "Inappropriate";
                    var message = "%s declaration '%s'.".formatted(problem, name);
                    throw ValueInstantiationException.from(context.getParser(), message, context.constructType(type));
                }
            }
        };
        var deserializer = new StdDeserializer<T>(type) {
            @Override
            public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                return type.cast(keyDeserializer.deserializeKey(parser.getValueAsString(), context));
            }
        };
        addKeyDeserializer(type, keyDeserializer);
        addDeserializer(type, deserializer);
    }

}
