package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

@RequiredArgsConstructor
@SuppressWarnings("ConverterNotAnnotatedInspection")
abstract class IntWrapperConverter<T extends IntWrapper> implements AttributeConverter<T, Integer> {

    private final IntFunction<T> factory;

    @Override
    public @Nullable T convertToEntityAttribute(@Nullable Integer dbData) {
        return dbData == null ? null : factory.apply(dbData);
    }

    @Override
    public @Nullable Integer convertToDatabaseColumn(@Nullable T attribute) {
        return attribute == null ? null : attribute.intValue();
    }

}
