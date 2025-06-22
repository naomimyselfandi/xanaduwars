package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.expression.*;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

record LambdaContext(
        @Delegate(excludes = Excludes.class) EvaluationContext parent,
        @Unmodifiable List<String> parameters,
        @Unmodifiable List<Object> arguments
) implements EvaluationContext {

    @SuppressWarnings("unused")
    private interface Excludes {
        Object lookupVariable(String name);
        void setVariable(String name, Object value);
        TypedValue assignVariable(String name, Supplier<TypedValue> valueSupplier);
    }

    LambdaContext(EvaluationContext parent, List<String> parameters, List<Object> arguments) {
        this.parent = parent;
        this.parameters = List.copyOf(parameters);
        this.arguments = List.copyOf(arguments);
    }

    @Override
    public @Nullable Object lookupVariable(String name) {
        var index = parameters.indexOf(name);
        return index >= 0 ? arguments.get(index) : parent.lookupVariable(name);
    }

    @Override
    public void setVariable(String name, @Nullable Object value) {
        if (parameters.contains(name)) {
            throw new UnsupportedOperationException("Reassigning lambda parameters is not supported.");
        } else {
            parent.setVariable(name, value);
        }
    }

    @Override
    public String toString() {
        return IntStream
                .range(0, Math.min(parameters.size(), arguments.size()))
                .mapToObj(i -> "%s=%s, ".formatted(parameters.get(i), arguments.get(i)))
                .collect(Collectors.joining("", "{", "...%s}".formatted(parent)));
    }

}

