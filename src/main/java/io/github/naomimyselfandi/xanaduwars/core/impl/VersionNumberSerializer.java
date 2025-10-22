package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;

import java.io.IOException;

class VersionNumberSerializer extends StdSerializer<Version> {

    VersionNumberSerializer() {
        super(Version.class);
    }

    @Override
    public void serialize(Version value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.findValueSerializer(VersionNumber.class).serialize(value.getVersionNumber(), gen, provider);
    }

}
