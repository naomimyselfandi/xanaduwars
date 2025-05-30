package io.github.naomimyselfandi.xanaduwars.account.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;

@Converter(autoApply = true)
final class ZoneIdConverter implements AttributeConverter<ZoneId, String> {

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable ZoneId attribute) {
        return attribute == null ? null : attribute.getId();
    }

    @Override
    public @Nullable ZoneId convertToEntityAttribute(@Nullable String dbData) {
        return dbData == null ? null : ZoneId.of(dbData);
    }

}
