package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.naomimyselfandi.xanaduwars.core.model.Specification;

import java.io.IOException;

class NameSerializer extends StdSerializer<Specification> {

    NameSerializer() {
        super(Specification.class);
    }

    @Override
    public void serialize(Specification value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getName());
    }

}
