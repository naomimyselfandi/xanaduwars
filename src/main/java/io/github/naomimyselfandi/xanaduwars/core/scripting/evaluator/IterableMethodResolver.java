package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

record IterableMethodResolver(MethodResolver delegate) implements MethodResolver {

    private static final MethodExecutor JOIN = (_, t, a) -> new TypedValue(
            Stream.concat(Stream.of(t), Arrays.stream(a)).flatMap(IterableMethodResolver::stream)
    );

    private static final MethodExecutor DROP = (_, t, a) -> {
        var elements = Arrays.stream(a).flatMap(IterableMethodResolver::stream).collect(Collectors.toSet());
        return new TypedValue(stream(t).filter(Predicate.not(elements::contains)));
    };

    private static final MethodExecutor LIST = (_, t, a) -> Stream
            .concat(Stream.of(t), Arrays.stream(a)).flatMap(IterableMethodResolver::stream)
            .collect(Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), TypedValue::new));

    private static final MethodExecutor SET = (_, t, a) -> Stream
            .concat(Stream.of(t), Arrays.stream(a)).flatMap(IterableMethodResolver::stream)
            .collect(Collectors.collectingAndThen(Collectors.toCollection(HashSet::new), TypedValue::new));

    @Override
    public @Nullable MethodExecutor resolve(
            EvaluationContext context,
            Object targetObject,
            String name,
            List<TypeDescriptor> argumentTypes
    ) throws AccessException {
        if (isIterableLike(targetObject)) {
            if (delegate.resolve(context, Stream.of(), name, argumentTypes) instanceof MethodExecutor e) {
                return (c, t, a) -> {
                    return e.execute(c, stream(t), a);
                };
            } else return switch (name) {
                case "join" -> JOIN;
                case "drop" -> DROP;
                case "list" -> LIST; // simplifies IterablePropertyAccessor
                case "set" -> SET;   // ditto
                default -> null;
            };
        } else {
            return null;
        }
    }

    private static boolean isIterableLike(@Nullable Object object) {
        return object instanceof Iterable<?> || object instanceof Stream<?> || object instanceof Map<?, ?>;
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

}
