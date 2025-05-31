package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.regex.Pattern;

final class RangeDeserializer extends StdDeserializer<Range> {

    private static final Pattern PATTERN = Pattern.compile("([0-9]+)-([0-9]+)");

    RangeDeserializer() {
        super(Range.class);
    }

    @Override
    public Range deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        record Helper(int min, int max) {}
        if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            var string = parser.getValueAsString();
            var matcher = PATTERN.matcher(string);
            if (matcher.matches()) {
                var min = Integer.parseInt(matcher.group(1));
                var max = Integer.parseInt(matcher.group(2));
                return new Range(min, max);
            } else {
                throw new JsonMappingException(parser, "Malformed range '%s'.".formatted(string));
            }
        } else {
            var helper = parser.readValueAs(Helper.class);
            return new Range(helper.min, helper.max);
        }
    }

    @Override
    public Range getNullValue(DeserializationContext context) {
        return Range.MELEE;
    }

}
