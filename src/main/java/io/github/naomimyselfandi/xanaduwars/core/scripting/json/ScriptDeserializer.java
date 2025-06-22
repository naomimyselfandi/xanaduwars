package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// A Jackson deserializer for scripts.
public class ScriptDeserializer extends StdDeserializer<Script> {

    /// A script that does nothing.
    public static final Script NULL = new ScriptImpl(new StatementWithBlock(List.of()));

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
        var token = parser.getCurrentToken();
        return switch (token) {
            case START_ARRAY -> {
                var accumulator = Stream.<String>builder();
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    accumulator.add(parser.getValueAsString());
                }
                yield SCRIPT_PARSER.apply(accumulator.build().collect(Collectors.joining("\n")));
            }
            case VALUE_STRING -> SCRIPT_PARSER.apply(parser.getValueAsString());
            case VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT, VALUE_TRUE, VALUE_FALSE -> new ScriptImpl(
                    new StatementWithReturnValue(new Expr(parser.getValueAsString()))
            );
            default -> throw new JsonMappingException(parser, "Unexpected token %s.".formatted(token));
        };
    }

}
