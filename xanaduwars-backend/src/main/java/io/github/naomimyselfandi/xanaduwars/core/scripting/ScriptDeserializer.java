package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ScriptDeserializer extends StdDeserializer<Script> {

    static final Script NULL = new ScriptImpl(List.of());

    private static final Function<String, Script> SCRIPT_PARSER = new ScriptParser();

    ScriptDeserializer() {
        super(Script.class);
    }

    @Override
    public Script getNullValue(DeserializationContext context) {
        return NULL;
    }

    @Override
    public Script deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
            var accumulator = Stream.<String>builder();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                accumulator.add(parser.getValueAsString());
            }
            return SCRIPT_PARSER.apply(accumulator.build().collect(Collectors.joining("\n")));
        } else if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            return SCRIPT_PARSER.apply(parser.getValueAsString());
        } else {
            return SCRIPT_PARSER.apply("return %s".formatted(parser.getValueAsString()));
        }
    }

}
