package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.*;

/// A conversion service that can convert any value to a boolean. This allows the
/// `&&` and `||` operators to be used as conditionals, which SpEl does not offer
/// natively. This conversion service also ensures that `null` is replaced with
/// empty optionals (due to limitations of SpEl's configuration, splitting this
/// into a separate class is complicated).
final class OmniBooleanConversionService extends DefaultConversionService {

    private static final Map<Class<?>, Object> NULL_REPLACEMENTS = Map.of(
            Optional.class, Optional.empty(),
            OptionalInt.class, OptionalInt.empty(),
            OptionalLong.class, OptionalLong.empty(),
            OptionalDouble.class, OptionalDouble.empty(),
            Boolean.class, false,
            boolean.class, false
    );

    OmniBooleanConversionService() {
        removeConvertible(String.class, Boolean.class); // the default throws for anything but "true" or "false"
        removeConvertible(Collection.class, Object.class); // the default is kind of weird, and breaks our conversion
        addConverter(Object.class, Boolean.class, OmniBooleanConversionService::isTrue);
    }

    private static boolean isTrue(Object o) {
        return switch (o) {
            case Optional<?> opt -> opt.isPresent() && isTrue(opt.get());
            case OptionalInt opt -> opt.isPresent() && isTrue(opt.getAsInt());
            case OptionalLong opt -> opt.isPresent() && isTrue(opt.getAsLong());
            case OptionalDouble opt -> opt.isPresent() && isTrue(opt.getAsDouble());
            case Number number -> number.doubleValue() < 0 || number.doubleValue() > 0;
            case String string -> !(string.isEmpty() || string.equals("false"));
            case Collection<?> collection -> !collection.isEmpty();
            case Map<?, ?> map -> !map.isEmpty();
            case Boolean bool -> bool;
            default -> true;
        };
    }

    @Override
    protected @Nullable Object convertNullSource(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        // DefaultConversionService bypasses the converters when the source is
        // null, so null -> false needs to be a special case. The default only
        // handles null -> Optional.empty(), so this also adds support for the
        // "primitive Optional" types.
        return NULL_REPLACEMENTS.get(targetType.getObjectType());
    }

}
