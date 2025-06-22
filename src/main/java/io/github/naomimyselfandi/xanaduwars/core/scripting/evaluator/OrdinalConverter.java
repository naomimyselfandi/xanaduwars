package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;

import java.util.Optional;
import java.util.Set;

record OrdinalConverter(TypeConverter delegate) implements TypeConverter {

    private static final Set<TypeDescriptor> INTS = Set.of(
            TypeDescriptor.valueOf(Integer.class),
            TypeDescriptor.valueOf(int.class)
    );

    @Override
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return delegate.canConvert(sourceType, targetType) || Optional
                .ofNullable(sourceType)
                .map(TypeDescriptor::getType)
                .filter(Ordinal.class::isAssignableFrom)
                .filter(_ -> INTS.contains(targetType))
                .isPresent();
    }

    @Override
    public @Nullable Object convertValue(
            @Nullable Object value,
            @Nullable TypeDescriptor sourceType,
            TypeDescriptor targetType
    ) {
        if (INTS.contains(targetType) && value instanceof Ordinal<?> ordinal) {
            return ordinal.ordinal();
        } else {
            return delegate.convertValue(value, sourceType, targetType);
        }
    }

}
