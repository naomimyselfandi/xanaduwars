package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;

import java.util.Arrays;
import java.util.List;

record FunctionImpl(
        ScriptRuntime runtime,
        EvaluationContext closure,
        String name,
        @Unmodifiable List<String> parameters,
        Statement body
) implements Function {

    FunctionImpl(ScriptRuntime runtime, EvaluationContext closure, String name, List<String> parameters, Statement body) {
        this.runtime = runtime;
        this.closure = closure;
        this.name = name;
        this.parameters = List.copyOf(parameters);
        this.body = body;
    }

    @Override
    public @Nullable Object call(@Nullable Object... arguments) {
        try {
            if (arguments.length == parameters.size()) {
                var context = new ScriptLocalContext(closure);
                for (var i = 0; i < arguments.length; i++) {
                    context.setVariable(parameters.get(i), arguments[i]);
                }
                return body.execute(runtime, context);
            } else {
                throw new EvaluationException("Wrong number of arguments.");
            }
        } catch (RuntimeException e) {
            var args = Arrays.toString(arguments);
            throw new EvaluationException("Calling %s with %s failed.".formatted(this, args), e);
        }
    }

    @Override
    public String toString() {
        return "%s(%s)".formatted(name, String.join(", ", parameters));
    }

}
