package io.github.naomimyselfandi.xanaduwars.testing;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LogicalArgumentsProvider implements ArgumentsProvider {

    private static final String UNSUPPORTED = "@LogicalSource methods must accept (only) three or more booleans.";

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        validate(context);
        var method = context.getRequiredTestMethod();
        var annotation = method.getAnnotation(LogicalSource.class);
        var operation = Objects.requireNonNull(annotation).value();
        var parameterCount = method.getParameterCount();
        var conditionCount = parameterCount - 1;
        return IntStream
                .range(0, 1 << conditionCount)
                .mapToObj(bitfield -> {
                    var booleans = IntStream
                            .range(0, conditionCount)
                            .mapToObj(index -> (bitfield & (1 << index)) != 0)
                            .collect(Collectors.toCollection(ArrayList::new));
                    var expected = booleans.stream().reduce(operation.definition).orElseThrow() ^ annotation.negated();
                    booleans.add(expected);
                    return booleans::toArray;
                });
    }

    private static void validate(ExtensionContext context) {
        context
                .getTestMethod()
                .filter(method -> method.getParameterCount() > 2)
                .filter(method -> Arrays
                        .stream(method.getParameterTypes())
                        .allMatch(LogicalArgumentsProvider::isBoolean))
                .filter(method -> method.isAnnotationPresent(LogicalSource.class))
                .orElseThrow(() -> new ParameterResolutionException(UNSUPPORTED));
    }

    private static boolean isBoolean(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

}
