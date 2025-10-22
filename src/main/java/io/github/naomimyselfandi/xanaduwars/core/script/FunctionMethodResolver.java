package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;

import java.util.List;

/// A method resolver that makes scripting functions easier to call. Ordinarily,
/// SpEl doesn't offer any user-configurable mechanism to make objects callable.
/// We work around this limit by detecting properties that resolve to functions.
/// Since we [treat local variables as properties][VariableAccessor], this helps
/// with local functions as well.
record FunctionMethodResolver(PropertyAccessor propertyAccessor) implements MethodResolver {

    @Override
    public @Nullable MethodExecutor resolve(
            EvaluationContext context,
            Object targetObject,
            String name,
            List<TypeDescriptor> argumentTypes
    ) throws AccessException {
        if (!propertyAccessor.canRead(context, targetObject, name)) {
            return null;
        }
        if (propertyAccessor.read(context, targetObject, name).getValue() instanceof Function function) {
            return (_, _, arguments) -> new TypedValue(function.call(arguments));
        } else {
            return null;
        }
    }

}
