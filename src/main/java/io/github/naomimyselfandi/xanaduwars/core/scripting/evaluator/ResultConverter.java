package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.TypeConverter;

import java.util.Set;

record ResultConverter(TypeConverter delegate) implements TypeConverter {

    private static final TypeDescriptor RESULT = TypeDescriptor.valueOf(Result.class);
    private static final Set<TypeDescriptor> BOOLEANS = Set.of(
            TypeDescriptor.valueOf(Boolean.class),
            TypeDescriptor.valueOf(boolean.class)
    );

    @Override
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return RESULT.equals(targetType)
                || (BOOLEANS.contains(targetType) && RESULT.equals(sourceType))
                || delegate.canConvert(sourceType, targetType);
    }

    @Override
    public @Nullable Object convertValue(
            @Nullable Object value,
            @Nullable TypeDescriptor sourceType,
            TypeDescriptor targetType
    ) {
        if (RESULT.equals(targetType) && !(value instanceof Result)) {
            return new Result.Fail(String.valueOf(value));
        } else if (BOOLEANS.contains(targetType) && value instanceof Result) {
            return value.equals(Result.okay());
        } else {
            return delegate.convertValue(value, sourceType, targetType);
        }
    }

}
