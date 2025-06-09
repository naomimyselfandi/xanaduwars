package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Converter(autoApply = true)
class MemoryConverter implements AttributeConverter<Memory, String> {

    private static final String DELIMITER = ",";

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable Memory attribute) {
        if (attribute == null) return null;
        var max = attribute.memory().keySet().stream().mapToInt(PlayerId::playerId).max().orElse(-1);
        return IntStream
                .rangeClosed(0, max)
                .mapToObj(PlayerId::new)
                .map(attribute.memory()::get)
                .map(id -> id == null ? "" : String.valueOf(id.index()))
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public @Nullable Memory convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null) return null;
        var parts = dbData.split(DELIMITER);
        var map = IntStream
                .range(0, parts.length)
                .boxed()
                .filter(i -> !parts[i].isEmpty())
                .collect(Collectors.toMap(PlayerId::new, i -> new StructureTypeId(Integer.parseInt(parts[i]))));
        return new Memory(map);
    }

}
