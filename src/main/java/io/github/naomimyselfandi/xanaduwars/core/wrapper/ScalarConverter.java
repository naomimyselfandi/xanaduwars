package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

@Converter(autoApply = true)
class ScalarConverter implements AttributeConverter<Scalar, Double> {

    @Override
    public @Nullable Scalar convertToEntityAttribute(@Nullable Double dbData) {
        return dbData == null ? null : Scalar.withDoubleValue(dbData);
    }

    @Override
    public @Nullable Double convertToDatabaseColumn(@Nullable Scalar attribute) {
        return attribute == null ? null : attribute.doubleValue();
    }

}
