package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Set;

final class TagSetDeserializer extends StdDeserializer<TagSet> {

    private final TypeReference<Set<Tag>> TAGS = new TypeReference<>() {};

    TagSetDeserializer() {
        super(TagSet.class);
    }

    @Override
    public TagSet deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            return TagSet.of(parser.readValueAs(Tag.class));
        } else {
            return new TagSet(parser.readValueAs(TAGS));
        }
    }

    @Override
    public TagSet getNullValue(DeserializationContext context) {
        return TagSet.EMPTY;
    }

}
