package io.github.naomimyselfandi.xanaduwars.core.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

final class FilterDeserializer extends StdDeserializer<Filter<?>> implements ContextualDeserializer {

    private static final Filter<Object> YES = new FilterImpl<>(new BiFilterYes<>());

    private final Class<?> input;

    @SuppressWarnings("unused")
    FilterDeserializer() {
        this(Object.class);
    }

    FilterDeserializer(Class<?> input) {
        super(Filter.class);
        this.input = input;
    }

    @Override
    public Filter<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        var javaType = context.getTypeFactory().constructParametricType(BiFilter.class, input, input);
        @SuppressWarnings("unchecked")
        var filter = (BiFilter<Object, Object>) context.readValue(parser, javaType);
        return new FilterImpl<>(filter);
    }

    @Override
    public Filter<?> getNullValue(DeserializationContext context) {
        return YES;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        var types = context.getContextualType().findTypeParameters(Filter.class);
        var input = types.length == 0 ? Object.class : types[0].getRawClass();
        return new FilterDeserializer(input);
    }

}
