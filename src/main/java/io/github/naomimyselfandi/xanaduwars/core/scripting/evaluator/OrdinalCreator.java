package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.TypeConverter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record OrdinalCreator(TypeConverter delegate) implements TypeConverter {

    private static final TypeDescriptor INT = TypeDescriptor.valueOf(Integer.class);

    private static final Set<TypeDescriptor> ORDINAL_TYPES;
    private static final List<Ordinal<?>> ZEROES;

    static {
        try (var scan = new ClassGraph().acceptPackages("io.github.naomimyselfandi.*").scan()) {
            ZEROES = scan
                    .getClassesImplementing(Ordinal.class)
                    .stream()
                    .map(ClassInfo::loadClass)
                    .<Ordinal<?>>flatMap(type -> {
                        try {
                            var field = type.getDeclaredField("ZERO");
                            field.setAccessible(true);
                            var ordinal = (Ordinal<?>) field.get(null);
                            return Stream.of(ordinal);
                        } catch (ClassCastException | ReflectiveOperationException _) {
                            return Stream.empty();
                        }
                    })
                    .toList();
        }
        ORDINAL_TYPES = ZEROES.stream().map(TypeDescriptor::forObject).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (INT.equals(sourceType) && ORDINAL_TYPES.contains(targetType)) {
            return true;
        } else {
            return delegate.canConvert(sourceType, targetType);
        }
    }

    @Override
    public @Nullable Object convertValue(
            @Nullable Object value,
            @Nullable TypeDescriptor sourceType,
            TypeDescriptor targetType
    ) {
        if (value instanceof Integer i && ORDINAL_TYPES.contains(targetType)) {
            return ZEROES
                    .stream()
                    .filter(targetType.getType()::isInstance)
                    .map(zero -> zero.withOrdinal(i))
                    .findFirst()
                    .orElseThrow();
        } else {
            return delegate.convertValue(value, sourceType, targetType);
        }
    }

}
