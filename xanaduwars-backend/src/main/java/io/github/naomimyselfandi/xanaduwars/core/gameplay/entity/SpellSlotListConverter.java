package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

@Converter(autoApply = true)
class SpellSlotListConverter implements AttributeConverter<SpellSlotList, String> {

    private static final String DELIMITER = ",";

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable SpellSlotList attribute) {
        if (attribute == null) return null;
        return attribute.slots().stream().map(SpellSlotData::toString).collect(Collectors.joining(DELIMITER));
    }

    @Override
    public @Nullable SpellSlotList convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null) return null;
        if (dbData.isEmpty()) return SpellSlotList.NONE;
        var slots = Arrays.stream(dbData.split(DELIMITER)).map(SpellSlotData::fromString).toList();
        return new SpellSlotList(slots);
    }

}
