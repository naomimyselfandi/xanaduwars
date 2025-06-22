package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import org.jetbrains.annotations.Nullable;
import org.springframework.expression.*;

import java.util.List;

record NullMethodPropertyAccessor(MethodResolver methodResolver) implements PropertyAccessor {

    // This happens automatically for actual nullary methods,
    // but not for "methods" synthesized by a MethodResolver.

    @Override
    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return target != null && methodResolver.resolve(context, target, name, List.of()) != null;
    }

    @Override
    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        if (target != null) {
            var executor = methodResolver.resolve(context, target, name, List.of());
            if (executor != null) return executor.execute(context, target);
        }
        throw new AccessException("Unable to access property '%s' on %s.".formatted(name, target));
    }

    @Override
    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) {
        return false;
    }

    @Override
    public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue)
            throws AccessException {
        throw new AccessException("Not supported.");
    }

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[0];
    }

}
