package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

record CustomMethodResolver(MethodResolver delegate) implements MethodResolver {

    private static final MethodExecutor JOIN = (_, t, a) -> new TypedValue(
            Stream.concat(Stream.of(t), Arrays.stream(a)).flatMap(CustomMethodResolver::stream)
    );

    private static final MethodExecutor DROP = (_, t, a) -> {
        var elements = Arrays.stream(a).flatMap(CustomMethodResolver::stream).collect(Collectors.toSet());
        return new TypedValue(stream(t).filter(Predicate.not(elements::contains)));
    };

    private static final MethodExecutor LIST = (_, t, a) -> Stream
            .concat(Stream.of(t), Arrays.stream(a)).flatMap(CustomMethodResolver::stream)
            .collect(Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), TypedValue::new));

    private static final MethodExecutor SET = (_, t, a) -> Stream
            .concat(Stream.of(t), Arrays.stream(a)).flatMap(CustomMethodResolver::stream)
            .collect(Collectors.collectingAndThen(Collectors.toCollection(HashSet::new), TypedValue::new));

    @Override
    public @Nullable MethodExecutor resolve(
            EvaluationContext context,
            Object targetObject,
            String name,
            List<TypeDescriptor> argumentTypes
    ) throws AccessException {
        if (delegate.resolve(context, targetObject, name, argumentTypes) instanceof MethodExecutor e) {
            return (c, t, a) -> unwrap(e.execute(c, t, a));
        } else if (delegate.resolve(context, Stream.of(), name, argumentTypes) instanceof MethodExecutor e) {
            return (c, t, a) -> unwrap(e.execute(c, stream(t), a));
        } else return switch (name) {
            case "join" -> JOIN;
            case "drop" -> DROP;
            case "list" -> LIST; // for CustomPropertyAccessor
            case "set" -> SET;   // ditto
            default -> null;
        };
    }

    private static Stream<?> stream(@Nullable Object object) {
        return switch (object) {
            case Stream<?> stream -> stream;
            case Collection<?> collection -> collection.stream();
            case Iterable<?> iterable -> StreamSupport.stream(iterable.spliterator(), false);
            case Map<?, ?> map -> map.entrySet().stream();
            case Optional<?> optional -> optional.stream();
            case null -> Stream.empty();
            default -> Stream.of(object);
        };
    }

    static TypedValue unwrap(TypedValue typedValue) {
        var value = typedValue.getValue();
        if (value instanceof Optional<?> it) {
            return new TypedValue(it.orElse(null));
        } else if (value instanceof Ordinal<?> it) {
            return new TypedValue(it.ordinal());
        } else {
            return typedValue;
        }
    }

}
