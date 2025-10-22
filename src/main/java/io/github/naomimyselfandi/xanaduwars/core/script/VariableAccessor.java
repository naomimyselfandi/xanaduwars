package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.expression.*;

/// A property accessor that makes local variables more ergonomic. Ordinarily,
/// SpEl requires all references to local variables to be prefixed with a hash
/// sign, to distinguish them from properties on the root object, and silently
/// returns `null` for undefined variables. Since scripts don't otherwise make
/// use of SpEl's root object, we can treat all defined variables as properties
/// to address both limitations. We also [use][FunctionMethodResolver] this
/// accessor to simplify calls to local functions.
final class VariableAccessor implements PropertyAccessor {

    private static final Class<?>[] ONLY_THE_ROOT = new Class<?>[]{Undefined.class};

    @Override
    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) {
        return target == Undefined.UNDEFINED;
    }

    @Override
    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) {
        return target == Undefined.UNDEFINED;
    }

    @Override
    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        var value = context.lookupVariable(name);
        throwIfUndefined(name, value);
        return new TypedValue(value);
    }

    @Override
    public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue)
            throws AccessException {
        throwIfUndefined(name, context.lookupVariable(name));
        context.setVariable(name, newValue);
    }

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return ONLY_THE_ROOT;
    }

    private static void throwIfUndefined(String name, @Nullable Object value) throws AccessException {
        if (value == Undefined.UNDEFINED) {
            throw new AccessException("Unknown variable '%s'.".formatted(name));
        }
    }

}
