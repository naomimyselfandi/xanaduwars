package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;

import java.util.Arrays;

record MethodReference(EvaluationContext context, String name) implements Function {

    @Override
    public @Nullable Object call(@Nullable Object... arguments) {
        if (arguments.length == 0) {
            throw new EvaluationException("Tried to invoke '%s' method reference on nothing.".formatted(name));
        }
        AccessException cause = null;
        var target = arguments[0];
        var args = Arrays.stream(arguments).skip(1).toArray();
        // TypeDescriptor.forObject(null) returns null, so we can't use toList() here.
        var types = Arrays.stream(args).map(TypeDescriptor::forObject).toArray(TypeDescriptor[]::new);
        if (target != null) {
            try {
                for (var resolver : context.getMethodResolvers()) {
                    var executor = resolver.resolve(context, target, name, Arrays.asList(types));
                    if (executor != null) {
                        return executor.execute(context, target, args).getValue();
                    }
                }
            } catch (AccessException e) {
                cause = e;
            }
        }
        var message = "Failed calling '%s' on %s with %s.".formatted(name, target, Arrays.toString(args));
        throw new EvaluationException(message, cause);
    }

}
