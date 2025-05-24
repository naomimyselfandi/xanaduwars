package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
@SuppressWarnings("ConverterNotAnnotatedInspection")
abstract class StringWrapperConverter<T extends StringWrapper> implements AttributeConverter<T, String> {

    private final Function<String, T> factory;

    @Override
    public @Nullable T convertToEntityAttribute(@Nullable String dbData) {
        return dbData == null ? null : factory.apply(dbData);
    }

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable T attribute) {
        return attribute == null ? null : attribute.toString();
    }

}
