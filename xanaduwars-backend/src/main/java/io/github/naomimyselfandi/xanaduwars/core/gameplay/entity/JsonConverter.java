package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Converter(autoApply = true)
class JsonConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable JsonNode attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public @Nullable JsonNode convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null) return null;
        try {
            return OBJECT_MAPPER.readTree(dbData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
