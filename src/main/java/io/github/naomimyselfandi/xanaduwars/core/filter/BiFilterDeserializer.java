package io.github.naomimyselfandi.xanaduwars.core.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.naomimyselfandi.xanaduwars.core.Ruleset;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

final class BiFilterDeserializer extends StdDeserializer<BiFilter<?, ?>> implements ContextualDeserializer {

    private static final BiFilter<Object, Object> YES = new BiFilterYes<>();

    private final @Nullable Ruleset ruleset;
    private final Class<?> subject, target;

    @SuppressWarnings("unused")
    BiFilterDeserializer() {
        this(null, Object.class, Object.class);
    }

    private BiFilterDeserializer(@Nullable Ruleset ruleset, Class<?> subject, Class<?> target) {
        super(Filter.class);
        this.ruleset = ruleset;
        this.subject = subject;
        this.target = target;
    }

    @Override
    public BiFilter<?, ?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            // Jackson is so weird sometimes
            parser.nextToken();
            return YES;
        }
        return new Parse(ruleset, parser.getValueAsString()).read(target, subject);
    }

    @Override
    public BiFilter<?, ?> getNullValue(DeserializationContext context) {
        return YES;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        var ruleset = ruleset(context, property);
        var types = context.getContextualType().findTypeParameters(BiFilter.class);
        var s = types.length == 0 ? Object.class : types[0].getRawClass();
        var t = types.length == 0 ? Object.class : types[0].getRawClass();
        return new BiFilterDeserializer(ruleset, s, t);
    }

    private static @Nullable Ruleset ruleset(DeserializationContext context, BeanProperty property) {
        try {
            return (Ruleset) context.findInjectableValue("ruleset", property, null);
        } catch (JsonMappingException | IllegalArgumentException _) {
            return null;
        }
    }

}
