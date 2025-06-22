package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.TypeConverter;

import java.util.Set;

record ResultCreator(TypeConverter delegate) implements TypeConverter {

    private static final Set<Class<?>> TARGETS = Set.of(Result.class, Result.Fail.class);

    @Override
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return TARGETS.contains(targetType.getType()) || delegate.canConvert(sourceType, targetType);
    }

    @Override
    public @Nullable Object convertValue(
            @Nullable Object value,
            @Nullable TypeDescriptor sourceType,
            TypeDescriptor targetType
    ) {
        if (TARGETS.contains(targetType.getType()) && !targetType.getType().isInstance(value)) {
            return new Result.Fail(String.valueOf(value));
        } else {
            return delegate.convertValue(value, sourceType, targetType);
        }
    }

}
