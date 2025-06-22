package io.github.naomimyselfandi.xanaduwars.testing;

import com.github.javafaker.Faker;
import io.github.naomimyselfandi.seededrandom.SeededRandom;
import jakarta.validation.constraints.*;
import lombok.SneakyThrows;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.mock;

/// An extension of [SeededRandom] with reflection support.
public class SeededRng extends SeededRandom {

    private static final Faker FAKER = Faker.instance();

    public SeededRng(long initialSeed) {
        super(initialSeed);
    }

    /// Get a random instance of some type.
    /// @apiNote In many cases, this method can infer the correct type. The
    /// varargs parameter exists only to allow this; it should not be used
    /// directly.
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> T get(T... reified) {
        assert reified.length == 0;
        return get((Class<T>) reified.getClass().componentType());
    }

    /// Get a random instance of some type.
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) instantiateClass(type);
    }

    /// Get a random instance of some type. The returned instance will not be
    /// one of the given values, as determined by `equals`.
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> T not(T... exclude) {
        return not((Class<T>) exclude.getClass().componentType(), exclude);
    }

    /// Get a random instance of some type. The returned instance will not be
    /// one of the given values, as determined by `equals`.
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> T not(Class<T> type, T... exclude) {
        T result;
        do {
            result = (T) instantiateClass(type);
        } while (Arrays.asList(exclude).contains(result));
        return result;
    }

    /// Get a random instance of some type.
    public Object get(AnnotatedType type) {
        return instantiateAnnotatedType(type);
    }

    public String nextString() {
        return nextUUID().toString().replaceAll("-", "");
    }

    public Instant nextInstant() {
        return Instant.ofEpochMilli(nextLong());
    }

    @SneakyThrows
    private Object instantiateClass(Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return nextInt();
        } else if (type == double.class || type == Double.class) {
            return nextDouble();
        } else if (type == boolean.class || type == Boolean.class) {
            return nextBoolean();
        } else if (hasMethod(type)) {
            return getClass().getMethod("next" + type.getSimpleName()).invoke(this);
        } else if (type.isEnum()) {
            return pick(type.getEnumConstants());
        } else if (type.isSealed()) {
            return instantiateClass(pick(type.getPermittedSubclasses()));
        } else if (type.isInterface()) {
            return instantiateMock(type);
        } else {
            var constructor = pick(type.getDeclaredConstructors());
            constructor.setAccessible(true);
            var values = Arrays
                    .stream(constructor.getAnnotatedParameterTypes())
                    .map(this::instantiateAnnotatedType)
                    .toArray();
            var result = constructor.newInstance(values);
            for (var method : type.getDeclaredMethods()) {
                if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                    try {
                        method.setAccessible(true);
                        method.invoke(result, instantiateAnnotatedType(method.getAnnotatedParameterTypes()[0]));
                    } catch (Exception _) {}
                }
            }
            return result;
        }
    }

    private Object instantiateAnnotatedType(AnnotatedType annotatedType) {
        var type = annotatedType.getType();
        if (annotatedType.isAnnotationPresent(Email.class)) {
            return FAKER.internet().emailAddress();
        } else if (annotatedType.isAnnotationPresent(Pattern.class)) {
            var annotation = annotatedType.getAnnotation(Pattern.class);
            assert annotation != null;
            // Faker doesn't support all regex metacharacters properly.
            return FAKER.regexify(annotation.regexp()).replaceAll("\\?:|^\\^|\\$$", "");
        } else if ((type == int.class || type == Integer.class)) {
            var range = ClosedRange.of(annotatedType);
            if (range != null) {
                var min = range.min;
                var max = range.max;
                // Constraint annotations use closed ranges, but nextInt() is a half-open range.
                return max == Integer.MAX_VALUE ? nextInt(min - 1, max) + 1 : nextInt(min, max + 1);
            } else {
                return nextInt();
            }
        } else {
            return instantiateType(type);
        }
    }

    private Object instantiateType(Type type) {
        return switch (type) {
            case Class<?> c -> instantiateClass(c);
            case ParameterizedType p -> instantiateParameterizedType(p);
            default -> throw new IllegalStateException(type.getTypeName());
        };
    }

    private Object instantiateParameterizedType(ParameterizedType parameterizedType) {
        var rawType = parameterizedType.getRawType();
        var arguments = parameterizedType.getActualTypeArguments();
        if (rawType == List.class || rawType == Collection.class || rawType == Iterable.class) {
            return List.of(instantiateType(arguments[0]), instantiateType(arguments[0]), instantiateType(arguments[0]));
        } else if (rawType == Set.class) {
            return Set.of(instantiateType(arguments[0]), instantiateType(arguments[0]), instantiateType(arguments[0]));
        } else if (rawType == Map.class) {
            return Map.of(instantiateType(arguments[0]), instantiateType(arguments[1]));
        } else if (rawType == Optional.class) {
            return Optional.ofNullable(nextBoolean() ? instantiateType(arguments[0]) : null);
        } else {
            return instantiateType(rawType);
        }
    }

    private boolean hasMethod(Class<?> type) {
        try {
            var method = getClass().getMethod("next" + type.getSimpleName());
            return type.isAssignableFrom(method.getReturnType());
        } catch (NoSuchMethodException _) {
            return false;
        }
    }

    private record ClosedRange(int min, int max) {

        static @Nullable ClosedRange of(AnnotatedType annotatedType) {
            var min = min(annotatedType);
            var max = max(annotatedType);
            if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
                return null;
            } else {
                return new ClosedRange(min, max);
            }
        }

        private static int min(AnnotatedType annotatedType) {
            if (annotatedType.isAnnotationPresent(Range.class)) {
                return (int) Objects.requireNonNull(annotatedType.getAnnotation(Range.class)).min();
            } else if (annotatedType.isAnnotationPresent(Positive.class)) {
                return 1;
            } else if (annotatedType.isAnnotationPresent(PositiveOrZero.class)) {
                return 0;
            } else if (annotatedType.isAnnotationPresent(Min.class)) {
                return (int) Objects.requireNonNull(annotatedType.getAnnotation(Min.class)).value();
            } else {
                return Integer.MIN_VALUE;
            }
        }

        private static int max(AnnotatedType annotatedType) {
            if (annotatedType.isAnnotationPresent(Range.class)) {
                return (int) Objects.requireNonNull(annotatedType.getAnnotation(Range.class)).max();
            } else if (annotatedType.isAnnotationPresent(Max.class)) {
                return (int) Objects.requireNonNull(annotatedType.getAnnotation(Max.class)).value();
            } else {
                return Integer.MAX_VALUE;
            }
        }

    }

    protected Object instantiateMock(Class<?> type) {
        return mock(type);
    }

}
