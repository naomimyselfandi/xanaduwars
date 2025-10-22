package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;
import io.github.naomimyselfandi.xanaduwars.core.service.VersionService;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@JsonComponent
class VersionDeserializer extends StdDeserializer<Version> {

    private final VersionService versionService;

    VersionDeserializer(@Lazy VersionService versionService) {
        super(Version.class);
        this.versionService = versionService;
    }

    @Override
    public Version deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return versionService.getVersion(parser.readValueAs(VersionNumber.class));
    }

}
